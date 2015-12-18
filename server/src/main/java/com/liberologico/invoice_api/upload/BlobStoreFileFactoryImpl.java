package com.liberologico.invoice_api.upload;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BlobStoreFileFactoryImpl implements BlobStoreFileFactory
{
    @Value( "${app.baseUrl}" )
    private String baseUrl;

    @Value( "${blob.containers-prefix}" )
    protected String containersPrefix;

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
