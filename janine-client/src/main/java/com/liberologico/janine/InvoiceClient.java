package com.liberologico.janine;

import com.google.gson.GsonBuilder;
import io.gsonfire.DateSerializationPolicy;
import io.gsonfire.GsonFireBuilder;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

public class InvoiceClient
{
    private static final Logger log = LoggerFactory.getLogger( InvoiceClient.class );

    private final Retrofit retrofit;

    public InvoiceClient( String baseUrl )
    {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor( new HttpLoggingInterceptor.Logger()
        {
            @Override
            public void log( String message )
            {
                log.info( message );
            }
        } );
        logging.setLevel( HttpLoggingInterceptor.Level.BASIC );

        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout( 30, TimeUnit.SECONDS )
                .readTimeout( 60, TimeUnit.SECONDS )
                .writeTimeout( 60, TimeUnit.SECONDS )
                .addInterceptor( logging )
                .build();

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
