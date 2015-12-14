package com.liberologico.invoice_api;

import com.liberologico.invoice_api.entities.Invoice;
import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Path;

public interface InvoiceService
{
    @POST( "/{prefix}/validate" )
    Call<Invoice> validate( @Path( "prefix" ) String prefix, @Body Invoice invoice );

    @POST( "/{prefix}" )
    Call<Invoice> generate( @Path( "prefix" ) String prefix, @Body Invoice invoice );
}
