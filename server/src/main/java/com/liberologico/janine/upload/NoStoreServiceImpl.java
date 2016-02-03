package com.liberologico.janine.upload;

import java.io.IOException;
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
    }

    @Override
    public void uploadFile( byte[] pdf, BlobStoreFile file ) throws IOException
    {
    }

    @Override
    public InputStream downloadFile( BlobStoreFile file ) throws IOException
    {
        return null;
    }
}
