package com.liberologico.janine.upload;

import com.google.common.io.ByteSource;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class BlobStoreServiceImpl implements StoreService
{
    private static final Logger log = LoggerFactory.getLogger( BlobStoreServiceImpl.class );

    @Autowired
    private BlobStoreContext api;

    @Override
    public boolean createContainer( String container )
    {
        log.info( "Creating container {}", container );
        return api.getBlobStore().createContainerInLocation( null, container );
    }

    @Override
    public void flushContainer( String container )
    {
        log.info( "Flushing container {}", container );
        if ( api.getBlobStore().containerExists( container ) )
        {
            api.getBlobStore().clearContainer( container );
        }
    }

    @Override
    public void uploadFile( byte[] pdf, BlobStoreFile file )
    {
        final String filename = file.getFilename();
        final String container = file.getContainer();

        if ( ! api.getBlobStore().containerExists( container ) )
        {
            this.createContainer( container );
        }

        uploadFile( pdf, filename, container );
    }

    private void uploadFile( byte[] object, String filename, String container )
    {
        log.info( "Uploading file {} on {}", filename, container );
        Payload payload = Payloads.newByteSourcePayload( ByteSource.wrap( object ) );
        api.getBlobStore().putBlob( container, api.getBlobStore().blobBuilder( filename ).payload( payload ).build() );
    }

    @Override
    public boolean exists( BlobStoreFile file )
    {
        try
        {
            return api.getBlobStore().getBlob( file.getContainer(), file.getFilename() ) != null;
        }
        catch ( ContainerNotFoundException e )
        {
            return false;
        }
    }

    @Override
    public InputStream downloadFile( BlobStoreFile file ) throws IOException
    {
        return downloadFile( file.getFilename(), file.getContainer() );
    }

    private InputStream downloadFile( String filename, String container ) throws IOException
    {
        log.info( "Downloading file {} on {}", filename, container );

        Blob blob = api.getBlobStore().getBlob( container, filename );

        if ( blob == null ) return null;

        return blob.getPayload().openStream();
    }
}
