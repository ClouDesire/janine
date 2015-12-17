package com.liberologico.invoice_api.unit;

import com.liberologico.invoice_api.upload.BlobStoreFile;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.assertEquals;

public class BlobStoreFileTest
{
    private BlobStoreFile file;

    @Before
    public void setUp() throws Exception
    {
        file = new BlobStoreFile( "http://invoice.api/", "invoices_", "malte", 1L );
    }

    @Test
    public void testGetFilename() throws Exception
    {
        assertEquals( "1.pdf", file.getFilename() );
    }

    @Test
    public void testGetContainer() throws Exception
    {
        assertEquals( "invoices_malte", file.getContainer() );
    }

    @Test
    public void testGetURI() throws Exception
    {
        assertEquals( URI.create( "http://invoice.api/malte/1.pdf" ), file.getURI() );
    }

    @Test
    public void testGetURIWithoutTrailingSlashInBaseURL() throws Exception
    {
        BlobStoreFile file = new BlobStoreFile( "http://invoice.api", "invoices_", "malte", 1L );
        assertEquals( URI.create( "http://invoice.api/malte/1.pdf" ), file.getURI() );
    }
}