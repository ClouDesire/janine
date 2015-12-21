package com.liberologico.janine.upload;

public interface BlobStoreFileFactory
{
    BlobStorePdf producePdf( String owner, Long id );

    BlobStoreJson produceJson( String owner, Long id );

    BlobStoreFile produce( String format, String prefix, Long id );
}
