package com.liberologico.invoice_api.upload;

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

    URL uploadFile( byte[] object, String filename, String container ) throws IOException;

    InputStream downloadFile( String filename, String container ) throws IOException;

    URL getFileURL( String filename, String container ) throws IOException;

    void deleteFile( String filename, String container );

    void deleteFile( String objectStorageFileName );

    enum ContainerVisibility
    {
        PUBLIC, PRIVATE
    }
}
