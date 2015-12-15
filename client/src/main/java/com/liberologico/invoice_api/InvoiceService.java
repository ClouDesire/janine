package com.liberologico.invoice_api;

import com.liberologico.invoice_api.entities.Invoice;
import com.squareup.okhttp.ResponseBody;
import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

import java.util.List;

public interface InvoiceService
{
    @POST( "/{prefix}/validate" )
    Call<Invoice> validate( @Path( "prefix" ) String prefix, @Body Invoice invoice );

    @POST( "/{prefix}" )
    Call<ResponseBody> generate( @Path( "prefix" ) String prefix, @Body Invoice invoice );

    @GET( "/fields" )
    Call<List<String>> getFields();
}
