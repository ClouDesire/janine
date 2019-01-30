package com.liberologico.janine.upload;

import java.io.InputStream;

/**
 * Dummy implementation
 */
public class NoStoreServiceImpl implements StoreService
{
    @Override
    public boolean createContainer( String container )
    {
        return false;
    }

    @Override
    public void flushContainer( String container )
    {
        // no flush
    }

    @Override
    public void uploadFile( byte[] pdf, BlobStoreFile file )
    {
        // no upload
    }

    @Override
    public boolean exists( BlobStoreFile file )
    {
        return false;
    }

    @Override
    public InputStream downloadFile( BlobStoreFile file )
    {
        return null;
    }
}
