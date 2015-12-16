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

    URL uploadFile( byte[] pdf, Long id, String prefix ) throws IOException;

    InputStream downloadFile( String filename, String container ) throws IOException;

    URL getFileURL( String filename, String container ) throws IOException;

    enum ContainerVisibility
    {
        PUBLIC, PRIVATE
    }
}
