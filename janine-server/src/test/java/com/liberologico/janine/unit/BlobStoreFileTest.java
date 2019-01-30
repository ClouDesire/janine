package com.liberologico.janine.unit;

import com.liberologico.janine.upload.BlobStoreFile;
import com.liberologico.janine.upload.BlobStoreJson;
import com.liberologico.janine.upload.BlobStorePdf;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.assertEquals;

public class BlobStoreFileTest
{
    private BlobStoreFile pdf;
    private BlobStoreFile json;

    @Before
    public void setUp()
    {
        pdf = new BlobStorePdf( "http://invoice.api/", "invoices_", "malte", 1L );
        json = new BlobStoreJson( "http://invoice.api/", "invoices_", "malte", 1L );
    }

    @Test
    public void testGetFilename()
    {
        assertEquals( "1.pdf", pdf.getFilename() );
        assertEquals( "1.json", json.getFilename() );
    }

    @Test
    public void testGetContainer()
    {
        assertEquals( "invoices_malte", pdf.getContainer() );
        assertEquals( "invoices_malte", json.getContainer() );
    }

    @Test
    public void testGetURI()
    {
        assertEquals( URI.create( "http://invoice.api/malte/1.pdf" ), pdf.getURI() );
        assertEquals( URI.create( "http://invoice.api/malte/1.json" ), json.getURI() );
    }

    @Test
    public void testGetURIWithoutTrailingSlashInBaseURL()
    {
        BlobStoreFile pdf = new BlobStorePdf( "http://invoice.api", "invoices_", "malte", 1L );
        assertEquals( URI.create( "http://invoice.api/malte/1.pdf" ), pdf.getURI() );

        BlobStoreFile json = new BlobStoreJson( "http://invoice.api", "invoices_", "malte", 1L );
        assertEquals( URI.create( "http://invoice.api/malte/1.json" ), json.getURI() );
    }
}