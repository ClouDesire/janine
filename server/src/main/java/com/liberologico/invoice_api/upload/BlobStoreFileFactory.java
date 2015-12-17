package com.liberologico.invoice_api.upload;

public interface BlobStoreFileFactory
{
    BlobStoreFile produce( String owner, Long id );
}
