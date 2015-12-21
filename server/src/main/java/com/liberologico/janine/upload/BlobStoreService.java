package com.liberologico.janine.upload;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public interface BlobStoreService
{
    void createPrivateContainer( String container );

    URL createPublicContainer( String container );

    void deleteContainer( String container );

    void flushContainer( String container );

    URL getCdnURL( String container );

    URL uploadFile( byte[] pdf, BlobStoreFile file ) throws IOException;

    InputStream downloadFile( BlobStoreFile file ) throws IOException;

    URL getFileURL( String filename, String container ) throws IOException;

    enum ContainerVisibility
    {
        PUBLIC, PRIVATE
    }
}