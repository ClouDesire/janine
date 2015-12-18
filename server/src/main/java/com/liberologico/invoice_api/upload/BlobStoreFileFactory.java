package com.liberologico.invoice_api.upload;

public interface BlobStoreFileFactory
{
    BlobStorePdf producePdf( String owner, Long id );

    BlobStoreJson produceJson( String owner, Long id );
}
