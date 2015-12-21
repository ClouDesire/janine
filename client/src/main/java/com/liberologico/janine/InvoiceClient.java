package com.liberologico.janine;

import com.google.gson.GsonBuilder;
import io.gsonfire.DateSerializationPolicy;
import io.gsonfire.GsonFireBuilder;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

public class InvoiceClient
{
    private final Retrofit retrofit;

    public InvoiceClient( String baseUrl )
    {
        retrofit = new Retrofit.Builder()
                .baseUrl( baseUrl )
                .addConverterFactory( GsonConverterFactory.create( getGsonBuilder().create() ) )
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
