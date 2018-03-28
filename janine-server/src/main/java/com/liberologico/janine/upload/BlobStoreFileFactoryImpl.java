package com.liberologico.janine.upload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class BlobStoreFileFactoryImpl implements BlobStoreFileFactory
{
    private static final Logger log = LoggerFactory.getLogger( BlobStoreFileFactoryImpl.class );

    @Value( "${app.baseUrl}" )
    private String baseUrl;

    @Value( "${blob.containers-prefix}" )
    private String containersPrefix;

    @PostConstruct
    public void setup()
    {
        log.info( "Using container prefix {}", containersPrefix );
    }

    @Override
    public BlobStoreFile produce( String format, String prefix, Long id )
    {
        switch ( format )
        {
            case "pdf": return producePdf( prefix, id );
            case "json": return produceJson( prefix, id );
            default: throw new IllegalArgumentException( format );
        }
    }

    @Override
    public BlobStorePdf producePdf( String owner, Long id )
    {
        return new BlobStorePdf( baseUrl, containersPrefix, owner, id );
    }

    @Override
    public BlobStoreJson produceJson( String owner, Long id )
    {
        return new BlobStoreJson( baseUrl, containersPrefix, owner, id );
    }
}
