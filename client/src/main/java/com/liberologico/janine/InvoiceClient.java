package com.liberologico.janine;

import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;
import io.gsonfire.DateSerializationPolicy;
import io.gsonfire.GsonFireBuilder;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

import java.util.concurrent.TimeUnit;

public class InvoiceClient
{
    private final Retrofit retrofit;

    public InvoiceClient( String baseUrl )
    {
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout( 30, TimeUnit.SECONDS ); // connect timeout
        client.setReadTimeout( 60, TimeUnit.SECONDS );    // socket timeout

        retrofit = new Retrofit.Builder()
                .baseUrl( baseUrl )
                .addConverterFactory( GsonConverterFactory.create( getGsonBuilder().create() ) )
                .client( client )
                .build();
    }

    public InvoiceService getService()
    {
        return retrofit.create( InvoiceService.class );
    }

    public static GsonBuilder getGsonBuilder()
    {
        return new GsonFireBuilder()
                .enableExposeMethodResult()
                .dateSerializationPolicy( DateSerializationPolicy.unixTimeMillis )
                .createGsonBuilder();
    }
}
