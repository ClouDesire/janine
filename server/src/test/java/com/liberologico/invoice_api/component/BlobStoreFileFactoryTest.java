package com.liberologico.invoice_api.component;

import com.liberologico.invoice_api.InvoiceApiApplication;
import com.liberologico.invoice_api.upload.BlobStoreFileFactory;
import com.liberologico.invoice_api.upload.BlobStoreJson;
import com.liberologico.invoice_api.upload.BlobStorePdf;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith( SpringJUnit4ClassRunner.class )
@SpringApplicationConfiguration( classes = InvoiceApiApplication.class )
public class BlobStoreFileFactoryTest
{
    @Autowired
    BlobStoreFileFactory factory;

    @Value( "${app.baseUrl}" )
    String baseUrl;

    @Value( "${blob.containers-prefix}" )
    String containersPrefix;

    @Test
    public void testProduce() throws Exception
    {
        final String owner = "malte";
        final Long id = 1L;
        assertEquals( new BlobStorePdf( baseUrl, containersPrefix, owner, id ), factory.producePdf( owner, id ) );
        assertEquals( new BlobStoreJson( baseUrl, containersPrefix, owner, id ), factory.produceJson( owner, id ) );
    }
}