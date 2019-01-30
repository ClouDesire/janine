package com.liberologico.janine.component;

import com.liberologico.janine.upload.BlobStoreFileFactory;
import com.liberologico.janine.upload.BlobStoreJson;
import com.liberologico.janine.upload.BlobStorePdf;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BlobStoreFileFactoryTest
{
    private static final String OWNER = "malte";
    private static final Long ID = 1L;

    @Autowired
    BlobStoreFileFactory factory;

    @Value( "${app.baseUrl}" )
    String baseUrl;

    @Value( "${blob.containers-prefix}" )
    String containersPrefix;

    @Test
    public void testProducePdf()
    {
        assertEquals( new BlobStorePdf( baseUrl, containersPrefix, OWNER, ID ), factory.producePdf( OWNER, ID ) );
    }

    @Test
    public void testProduceJson()
    {
        assertEquals( new BlobStoreJson( baseUrl, containersPrefix, OWNER, ID ), factory.produceJson( OWNER, ID ) );
    }

    @Test
    public void testProduce()
    {
        assertEquals( BlobStorePdf.class, factory.produce( "pdf", OWNER, ID ).getClass() );
        assertEquals( BlobStoreJson.class, factory.produce( "json", OWNER, ID ).getClass() );
    }
}
