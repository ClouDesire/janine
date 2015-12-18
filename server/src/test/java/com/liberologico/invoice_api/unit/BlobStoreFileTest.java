package com.liberologico.invoice_api.unit;

import com.liberologico.invoice_api.upload.BlobStoreFile;
import com.liberologico.invoice_api.upload.BlobStoreJson;
import com.liberologico.invoice_api.upload.BlobStorePdf;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.assertEquals;

public class BlobStoreFileTest
{
    private BlobStoreFile pdf;
    private BlobStoreFile json;

    @Before
    public void setUp() throws Exception
    {
        pdf = new BlobStorePdf( "http://invoice.api/", "invoices_", "malte", 1L );
        json = new BlobStoreJson( "http://invoice.api/", "invoices_", "malte", 1L );
    }

    @Test
    public void testGetFilename() throws Exception
    {
        assertEquals( "1.pdf", pdf.getFilename() );
        assertEquals( "1.json", json.getFilename() );
    }

    @Test
    public void testGetContainer() throws Exception
    {
        assertEquals( "invoices_malte", pdf.getContainer() );
        assertEquals( "invoices_malte", json.getContainer() );
    }

    @Test
    public void testGetURI() throws Exception
    {
        assertEquals( URI.create( "http://invoice.api/malte/1.pdf" ), pdf.getURI() );
        assertEquals( URI.create( "http://invoice.api/malte/1.json" ), json.getURI() );
    }

    @Test
    public void testGetURIWithoutTrailingSlashInBaseURL() throws Exception
    {
        BlobStoreFile pdf = new BlobStorePdf( "http://invoice.api", "invoices_", "malte", 1L );
        assertEquals( URI.create( "http://invoice.api/malte/1.pdf" ), pdf.getURI() );

        BlobStoreFile json = new BlobStoreJson( "http://invoice.api", "invoices_", "malte", 1L );
        assertEquals( URI.create( "http://invoice.api/malte/1.json" ), json.getURI() );
    }
}