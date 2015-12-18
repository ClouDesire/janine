package com.liberologico.invoice_api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.liberologico.invoice_api.entities.Address;
import com.liberologico.invoice_api.entities.Invoice;
import com.liberologico.invoice_api.entities.Line;
import com.liberologico.invoice_api.entities.Person;
import com.liberologico.invoice_api.entities.Price;
import com.liberologico.invoice_api.upload.BlobStoreService;
import com.squareup.okhttp.ResponseBody;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import redis.clients.jedis.Jedis;
import retrofit.Call;
import retrofit.Response;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith( SpringJUnit4ClassRunner.class )
@SpringApplicationConfiguration( classes = InvoiceApiApplication.class )
@WebIntegrationTest ("server.port=0")
public class InvoiceApiApplicationTests
{
    private static final String ROOT = "http://localhost";
    private static final String PREFIX = "TEST";

    @Value ("${local.server.port}")
    int port;

    private InvoiceService service;

    @Autowired
    private JedisConnectionFactory jedisConnectionFactory;

    @Autowired
    private BlobStoreService blobStoreService;

    @Autowired
    private ObjectMapper objectMapper;

    @Value( "${blob.containers-prefix}" )
    protected String containersPrefix;

    @Value( "${app.baseUrl}" )
    protected String baseUrl;

    @Before
    @After
    public void reset()
    {
        service = new InvoiceClient( ROOT + ":" + port ).getService();

        Jedis jedis = new Jedis( jedisConnectionFactory.getShardInfo() );
        jedis.flushAll();
        jedis.close();

        final String container = containersPrefix + PREFIX;
        blobStoreService.flushContainer( container );
    }

    @Test
    public void emptyBody() throws IOException
    {
        Date now = new Date();

        Call<Invoice> call = service.validate( PREFIX, new Invoice() );

        Response<Invoice> response = call.execute();
        assertFalse( response.isSuccess() );
        assertEquals( 400, response.code() );
        assertNotNull( response.errorBody() );

        Gson gson = InvoiceClient.getGsonBuilder().create();
        ApiError errorBody = gson.fromJson( response.errorBody().string(), ApiError.class );
        assertNotNull( errorBody );
        assertTrue( now.before( errorBody.timestamp ) );
    }

    @Test
    public void simpleInvoice() throws IOException
    {
        Invoice invoice = getInvoice(
                new Line().setDescription( "Riga 1" )
                          .setPrice( new Price().setPrice( BigDecimal.TEN ).setCurrency( "EUR" ) ),
                new Line().setDescription( "Riga 2" )
                          .setPrice( new Price().setPrice( BigDecimal.ONE ).setCurrency( "EUR" ) )
        );

        Call<ResponseBody> call = service.generate( PREFIX, invoice );

        testPdfResponse( call );
    }

    private void testPdfResponse( Call<ResponseBody> call ) throws IOException
    {
        Response<ResponseBody> response = call.execute();
        assertTrue( response.isSuccess() );
        assertEquals( 200, response.code() );
        assertNotNull( response.body() );

        try ( PDDocument pdf = PDDocument.load( response.body().bytes() ) )
        {
            assertTrue( pdf.isEncrypted() );
        }
        catch ( IOException e )
        {
            fail( e.getMessage() );
        }
    }

    private Invoice testJsonResponse( Call<ResponseBody> call ) throws IOException
    {
        Response<ResponseBody> response = call.execute();
        assertTrue( response.isSuccess() );
        assertEquals( 200, response.code() );
        assertNotNull( response.body() );

        try
        {
            return objectMapper.readValue( response.body().bytes(), Invoice.class );
        }
        catch ( IOException e )
        {
            fail( e.getMessage() );
            throw e;
        }
    }

    @Test
    public void simpleInvoiceUrl() throws IOException
    {
        Invoice invoice = getInvoice(
                new Line().setDescription( "Riga 1" )
                        .setPrice( new Price().setPrice( BigDecimal.TEN ).setCurrency( "EUR" ) ),
                new Line().setDescription( "Riga 2" )
                        .setPrice( new Price().setPrice( BigDecimal.ONE ).setCurrency( "EUR" ) )
        );

        Call<ResponseBody> call = service.generateAndUpload( PREFIX, invoice );

        Response<ResponseBody> response = call.execute();
        assertTrue( response.isSuccess() );
        assertEquals( 201, response.code() );

        final String location = response.headers().get( "Location" );
        assertNotNull( location );
        assertEquals( baseUrl + PREFIX + "/1.pdf", location );

        testPdfResponse( service.downloadPdf( PREFIX, 1L ) );
        assertEquals( invoice.getDate(), testJsonResponse( service.downloadJson( PREFIX, 1L ) ).getDate() );
    }

    @Test
    public void getFields() throws IOException
    {
        Call<List<String>> call = service.getFields();
        Response<List<String>> response = call.execute();
        assertTrue( response.isSuccess() );
        assertEquals( 200, response.code() );
        final List<String> fields = response.body();
        assertNotNull( fields );
        assertFalse( fields.isEmpty() );
    }

    public Invoice getInvoice( Line... lines )
    {
        final Person holder = new Person()
                .setFirstName( "Antanio" )
                .setLastName( "Divani" )
                .setEmail( "bu@del.lo" )
                .setAddress( new Address( "address", "city", "country", "state", "zip" ) );
        final Person recipient = new Person()
                .setFirstName( "Brebuzio" )
                .setLastName( "Sfanti" )
                .setEmail( "di@tu.ma" )
                .setAddress( new Address( "address", "city", "country", "state", "zip" ) );
        return new Invoice()
                .setHolder( holder )
                .setRecipient( recipient )
                .setLines( Arrays.asList( lines ) );
    }
}
