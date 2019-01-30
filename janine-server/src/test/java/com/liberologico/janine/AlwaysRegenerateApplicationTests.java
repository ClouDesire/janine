package com.liberologico.janine;

import com.liberologico.janine.entities.Invoice;
import org.junit.Test;
import org.springframework.test.context.TestPropertySource;
import retrofit2.Call;
import retrofit2.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@TestPropertySource( properties = "app.alwaysRegenerate=true" )
public class AlwaysRegenerateApplicationTests extends ApplicationTests
{
    @Test
    public void generateInvoiceWithRegenerationEnabled() throws Exception
    {
        Invoice invoice = getInvoice();
        Call<Long> call = service.generateAndUpload( PREFIX, 42L, invoice );

        Response<Long> response = call.clone().execute();
        assertTrue( response.isSuccessful() );
        assertEquals( 201, response.code() );
        assertEquals( 42L, response.body().longValue() );

        response = call.clone().execute();
        assertTrue( response.isSuccessful() );
        assertEquals( 201, response.code() );
        assertEquals( 42L, response.body().longValue() );
    }
}
