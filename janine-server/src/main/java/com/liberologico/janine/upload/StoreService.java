package com.liberologico.janine.upload;

import java.io.IOException;
import java.io.InputStream;

public interface StoreService
{
    boolean createContainer( String container );

    void flushContainer( String container );

    void uploadFile( byte[] pdf, BlobStoreFile file ) throws IOException;

    boolean exists( BlobStoreFile file ) throws IOException;

    InputStream downloadFile( BlobStoreFile file ) throws IOException;
}
