package com.liberologico.janine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.liberologico.janine.entities.Address;
import com.liberologico.janine.entities.Holder;
import com.liberologico.janine.entities.Invoice;
import com.liberologico.janine.entities.Line;
import com.liberologico.janine.entities.Person;
import com.liberologico.janine.entities.Price;
import com.liberologico.janine.entities.Recipient;
import com.liberologico.janine.upload.StoreService;
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
    private StoreService storeService;

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
        storeService.flushContainer( container );
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
    public void invalidRecipient() throws IOException
    {
        Invoice invoice = getInvoice(
                new Line().setDescription( "Riga 0" ).setPrice( new Price().setPrice( BigDecimal.ONE ) )
        );
        invoice.getRecipient().setFirstName( null );
        invoice.getRecipient().setLastName( null );

        Call<Invoice> call = service.validate( PREFIX, invoice );

        Response<Invoice> response = call.execute();
        assertFalse( response.isSuccess() );
        assertEquals( 400, response.code() );
        assertNotNull( response.errorBody() );
    }

    @Test
    public void zeroQuantity() throws IOException
    {
        Invoice invoice = getInvoice( new Line().setDescription( "Riga 0" )
                                                .setPrice( new Price().setPrice( BigDecimal.ZERO ) )
                                                .setQuantity( BigDecimal.ZERO ) );

        Call<Invoice> call = service.validate( PREFIX, invoice );

        Response<Invoice> response = call.execute();
        assertTrue( response.isSuccess() );
    }

    @Test
    public void simpleInvoice() throws IOException
    {
        Invoice invoice = getInvoice(
                new Line().setDescription( "Riga 1" ).setPrice( new Price().setPrice( BigDecimal.TEN ) ),
                new Line().setDescription( "Riga 2" ).setPrice( new Price().setPrice( BigDecimal.ONE ) )
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
                new Line().setDescription( "Riga 1" ).setPrice( new Price().setPrice( BigDecimal.TEN ) ),
                new Line().setDescription( "Riga 2" ).setPrice( new Price().setPrice( BigDecimal.ONE ) )
        );

        Call<Long> call = service.generateAndUpload( PREFIX, invoice );

        Response<Long> response = call.execute();
        assertTrue( response.isSuccess() );
        assertEquals( 201, response.code() );

        assertEquals( 1L, response.body().longValue() );

        final String location = response.headers().get( "Location" );
        assertNotNull( location );
        assertEquals( baseUrl + PREFIX + "/1.pdf", location );

        testPdfResponse( service.downloadPdf( PREFIX, 1L ) );
        assertEquals( invoice.getDate(), testJsonResponse( service.downloadJson( PREFIX, 1L ) ).getDate() );
    }

    @Test
    public void badInvoice() throws IOException
    {
        Invoice invoice = new Invoice();

        Call<ResponseBody> call = service.generate( PREFIX, invoice );
        Response<ResponseBody> response = call.execute();
        assertFalse(response.isSuccess());

        Gson gson = InvoiceClient.getGsonBuilder().create();
        ApiError apiError = gson.fromJson( response.errorBody().string(), ApiError.class );
        assertEquals( new Integer(400), apiError.status );
        assertEquals( 4, apiError.errors.size() );
    }

    @Test
    public void fourOhFourPdf() throws IOException
    {
        Call<ResponseBody> call = service.downloadPdf( PREFIX, 1L );
        Response<ResponseBody> response = call.execute();
        assertFalse( response.isSuccess() );
        assertEquals( 404, response.code() );
    }

    @Test
    public void fourOhFourJson() throws IOException
    {
        Call<ResponseBody> call = service.downloadJson( PREFIX, 1L );
        Response<ResponseBody> response = call.execute();
        assertFalse( response.isSuccess() );
        assertEquals( 404, response.code() );
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

    @Test
    public void getInvoiceSchema() throws IOException
    {
        Call<Object> call = service.getInvoiceSchema();
        Response<Object> response = call.execute();
        assertTrue( response.isSuccess() );
        assertEquals( 200, response.code() );
        final Object schema = response.body();
        assertNotNull( schema );
    }

    public Invoice getInvoice( Line... lines )
    {
        final Person holder = new Holder()
                .setAddress( new Address( "address", "city", "country", "state", "zip" ) )
                .setCompanyName( "Caffè Toraldo" )
                .setTaxCode( "CFTGNN" );
        final Person recipient = new Recipient()
                .setFirstName( "Brebuzio" )
                .setLastName( "Sfanti" )
                .setEmail( "di@tu.ma" )
                .setAddress( new Address( "address", "city", "country", "state", "zip" ) );
        return new Invoice()
                .setHolder( (Holder) holder )
                .setRecipient( (Recipient) recipient )
                .setCurrency( "EUR" )
                .setVat( new BigDecimal( 22 ) )
                .setLines( Arrays.asList( lines ) );
    }
}
