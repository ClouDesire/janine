package com.liberologico.invoice_api.upload;

import com.google.common.io.ByteSource;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.openstack.swift.v1.domain.SwiftObject;
import org.jclouds.openstack.swift.v1.features.ObjectApi;
import org.jclouds.rackspace.cloudfiles.v1.CloudFilesApi;
import org.jclouds.rackspace.cloudfiles.v1.domain.CDNContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings ( "Duplicates" )
@Component
public class BlobStoreServiceImpl implements BlobStoreService
{
    private static final Logger log = LoggerFactory.getLogger( BlobStoreServiceImpl.class );
    private static final String REGION = "LON";

    @Autowired
    private CloudFilesApi api;

    @Value ( "${blob.enabled}" )
    private Boolean blobUploadEnabled;
    @Value ( "${blob.containers-prefix}" )
    protected String containersPrefix;

    private Map<String, URL> urlCache = new ConcurrentHashMap<>();

    @Override
    public void createPrivateContainer( String container )
    {
        this.createContainer( container, ContainerVisibility.PRIVATE );
    }

    @Override
    public URL createPublicContainer( String container )
    {
        return this.createContainer( container, ContainerVisibility.PUBLIC );
    }

    private URL createContainer( String container, ContainerVisibility visibility )
    {
        log.info( "Creating container {}", container );
        api.getContainerApi( REGION ).create( container );
        if ( ContainerVisibility.PUBLIC.equals( visibility ) )
            return this.enableCdnOnContainerReturnURI( container );
        return null;
    }

    @Override
    public void deleteContainer( String container )
    {
        log.info( "Deleting container {}", container );
        api.getContainerApi( REGION ).deleteIfEmpty( container );
    }

    @Override
    public void flushContainer( String container )
    {
        log.info( "Flushing container {}", container );
        if ( api.getContainerApi( REGION ).get( container ) == null )
            return;

        ObjectApi objectApi = api.getObjectApi( REGION, container );
        List<SwiftObject> objects = objectApi.list();
        for ( SwiftObject object : objects )
        {
            log.debug( "Deleting {}", object.getName() );
            objectApi.delete( object.getName() );
        }
    }

    @Override
    public URL getCdnURL( String container )
    {
        URL entry = urlCache.get( container );
        if ( entry == null )
        {
            // maybe a synchronization to avoid wasting calls would be great
            log.info( "Get CDN URL for container {}", container );
            try
            {
                CDNContainer cdnContainer = api.getCDNApi( REGION ).get( container );
                if (cdnContainer == null)
                {
                    this.createPublicContainer( container );
                    cdnContainer = api.getCDNApi( REGION ).get( container );
                }
                entry = cdnContainer.getSslUri().toURL();
            }
            catch ( MalformedURLException e )
            {
                log.error( "Malform url returned", e );
                return null;
            }
            urlCache.put( container, entry );
        }
        return entry;
    }

    private URL enableCdnOnContainerReturnURI( String container )
    {
        log.info( "Enable CDN on container {}", container );
        api.getCDNApi( REGION ).enable( container );
        return getCdnURL( container );
    }

    @Override
    public synchronized URL uploadFile( byte[] pdf, BlobStoreFile file ) throws IOException
    {
        final String filename = file.getFilename();
        final String container = file.getContainer( containersPrefix );

        if ( api.getContainerApi( REGION ).get( container ) == null )
        {
            this.createPrivateContainer( container );
        }

        return uploadFile( pdf, filename, container );
    }

    private URL uploadFile( byte[] object, String filename, String container ) throws IOException
    {
        ObjectApi objectApi = api.getObjectApi( REGION, container );

        Payload payload = Payloads.newByteSourcePayload( ByteSource.wrap( object ) );

        log.info( "Uploading file {} on {}", filename, container );

        objectApi.put( filename, payload );

        return getFileURL( filename, container );
    }

    @Override
    public InputStream downloadFile( BlobStoreFile file ) throws IOException
    {
        return downloadFile( file.getFilename(), file.getContainer( containersPrefix ) );
    }

    private InputStream downloadFile( String filename, String container ) throws IOException
    {
        ObjectApi objectApi = api.getObjectApi( REGION, container );

        log.info( "Downloading file {} on {}", filename, container );

        return objectApi.get( filename ).getPayload().openStream();
    }

    @Override
    public URL getFileURL( String filename, String container ) throws IOException
    {
        ObjectApi objectApi = api.getObjectApi( REGION, container );

        log.info( "Fetching file {} on {}", filename, container );

        SwiftObject object = objectApi.get( filename );
        if ( object != null )
        {
            CDNContainer cdnContainer = api.getCDNApi( REGION ).get( container );
            if ( cdnContainer != null && cdnContainer.isEnabled() )
            {
                return new URL( cdnContainer.getSslUri().toURL(), filename );
            }
            return object.getUri().toURL();
        }
        return null;
    }
}
