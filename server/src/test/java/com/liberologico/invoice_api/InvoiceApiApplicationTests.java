package com.liberologico.invoice_api;

import com.google.gson.Gson;
import com.liberologico.invoice_api.entities.Invoice;
import com.liberologico.invoice_api.entities.Line;
import com.liberologico.invoice_api.entities.Person;
import com.liberologico.invoice_api.entities.Price;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import retrofit.Call;
import retrofit.Response;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith( SpringJUnit4ClassRunner.class )
@SpringApplicationConfiguration( classes = InvoiceApiApplication.class )
@WebAppConfiguration
public class InvoiceApiApplicationTests
{
    private static final String ROOT = "http://localhost:8080";

    private final InvoiceService service = new InvoiceClient( ROOT ).getService();

    @Test
    public void emptyBody() throws IOException
    {
        Date now = new Date();

        Call<Invoice> call = service.validate( new Invoice() );

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
        Invoice invoice = new Invoice()
                .setHolder( new Person().setEmail( "bu@del.lo" ) )
                .setRecipient( new Person().setEmail( "di@tu.ma" ) )
                .setLines( Arrays.asList(
                    new Line().setDescription( "Riga 1" )
                              .setPrice( new Price().setPrice( BigDecimal.TEN ).setCurrency( "EUR" ) ),
                    new Line().setDescription( "Riga 2" )
                              .setPrice( new Price().setPrice( BigDecimal.ONE ).setCurrency( "EUR" ) )
                ) );

        Call<Invoice> call = service.validate( invoice );

        Response<Invoice> response = call.execute();
        assertTrue( response.isSuccess() );
        assertEquals( 200, response.code() );
        assertNotNull( response.body() );
    }
}
