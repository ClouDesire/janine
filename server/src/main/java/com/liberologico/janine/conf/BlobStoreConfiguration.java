package com.liberologico.janine.conf;

import org.apache.commons.lang3.StringUtils;
import org.jclouds.ContextBuilder;
import org.jclouds.rackspace.cloudfiles.v1.CloudFilesApi;
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
    private static final String API = "rackspace-cloudfiles-uk";

    @Value ( "${blob.identity}" )
    private String identity;
    @Value ( "${blob.credential}" )
    private String credential;
    @Value ( "${blob.connection-timeout}" )
    private int connectionTimeout;
    @Value ( "${blob.so-timeout}" )
    private int soTimeout;
    @Value ( "${blob.max-retries}" )
    private int maxRetries;

    @Bean
    public CloudFilesApi cloudFilesApi()
    {
        Properties overrides = new Properties();

        if ( StringUtils.isEmpty( identity ) || StringUtils.isEmpty( credential ) )
            log.warn( "Blank credentials, upload won't work!" );

        overrides.put( PROPERTY_CONNECTION_TIMEOUT, connectionTimeout );
        overrides.put( PROPERTY_SO_TIMEOUT, soTimeout );
        overrides.put( PROPERTY_MAX_RETRIES, maxRetries );

        return ContextBuilder.newBuilder( API )
                .credentials( identity, credential )
                .overrides( overrides )
                .buildApi( CloudFilesApi.class );
    }
}
