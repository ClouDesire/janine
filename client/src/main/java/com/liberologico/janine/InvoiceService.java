package com.liberologico.janine;

import com.liberologico.janine.entities.Invoice;
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

    @POST( "/{prefix}/download" )
    Call<ResponseBody> generate( @Path( "prefix" ) String prefix, @Body Invoice invoice );

    @POST( "/{prefix}" )
    Call<Long> generateAndUpload( @Path( "prefix" ) String prefix, @Body Invoice invoice );

    @GET( "/{prefix}/{id}.pdf" )
    Call<ResponseBody> downloadPdf( @Path( "prefix" ) String prefix, @Path( "id" ) Long id );

    @GET( "/{prefix}/{id}.json" )
    Call<ResponseBody> downloadJson( @Path( "prefix" ) String prefix, @Path( "id" ) Long id );

    @GET( "/fields" )
    Call<List<String>> getFields();

    @GET( "/schema" )
    Call<Object> getInvoiceSchema();
}
