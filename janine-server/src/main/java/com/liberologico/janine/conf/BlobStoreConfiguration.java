package com.liberologico.janine.conf;

import com.liberologico.janine.upload.BlobStoreServiceImpl;
import com.liberologico.janine.upload.NoStoreServiceImpl;
import com.liberologico.janine.upload.StoreService;
import org.apache.commons.lang3.StringUtils;
import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStoreContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

import static org.jclouds.Constants.PROPERTY_CONNECTION_TIMEOUT;
import static org.jclouds.Constants.PROPERTY_MAX_RETRIES;
import static org.jclouds.Constants.PROPERTY_SO_TIMEOUT;

@Configuration
public class BlobStoreConfiguration
{
    private static final Logger log = LoggerFactory.getLogger( BlobStoreConfiguration.class );

    @Value( "${blob.provider}" )
    private String provider;
    @Value( "${blob.identity}" )
    private String identity;
    @Value( "${blob.credential}" )
    private String credential;
    @Value( "${blob.connection-timeout}" )
    private int connectionTimeout;
    @Value( "${blob.so-timeout}" )
    private int soTimeout;
    @Value( "${blob.max-retries}" )
    private int maxRetries;
    @Value( "${blob.enabled}" )
    private Boolean blobUploadEnabled;

    @Bean
    public BlobStoreContext blobStoreContext()
    {
        Properties overrides = new Properties();
        overrides.put( PROPERTY_CONNECTION_TIMEOUT, connectionTimeout );
        overrides.put( PROPERTY_SO_TIMEOUT, soTimeout );
        overrides.put( PROPERTY_MAX_RETRIES, maxRetries );

        return ContextBuilder.newBuilder( provider )
                .credentials( identity, credential )
                .overrides( overrides )
                .buildApi( BlobStoreContext.class );
    }

    @Bean
    public StoreService storeService()
    {
        if ( StringUtils.isEmpty( identity ) || StringUtils.isEmpty( credential ) || ! blobUploadEnabled )
        {
            log.warn( "Blank credentials, upload won't work!" );
            return new NoStoreServiceImpl();
        }

        return new BlobStoreServiceImpl();
    }
}
