package com.liberologico.invoice_api;

import com.liberologico.invoice_api.entities.Invoice;
import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.POST;

public interface InvoiceService
{
    @POST("/")
    Call<Invoice> post( @Body Invoice invoice );
}
