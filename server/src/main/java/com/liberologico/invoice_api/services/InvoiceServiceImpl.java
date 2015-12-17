package com.liberologico.invoice_api.services;

import com.liberologico.invoice_api.entities.Invoice;
import com.liberologico.invoice_api.exceptions.InvoiceServiceException;
import com.liberologico.invoice_api.pdf.PdfService;
import com.liberologico.invoice_api.upload.BlobStoreFile;
import com.liberologico.invoice_api.upload.BlobStoreFileFactory;
import com.liberologico.invoice_api.upload.BlobStoreService;
import org.apache.pdfbox.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@Component
public class InvoiceServiceImpl implements InvoiceService
{
    @Autowired
    private BlobStoreFileFactory blobStoreFileFactory;

    @Autowired
    private BlobStoreService blobStoreService;

    @Autowired
    private PdfService pdfService;

    private final Jedis jedis;

    @Autowired
    public InvoiceServiceImpl( JedisConnectionFactory connectionFactory )
    {
        this.jedis = new Jedis( connectionFactory.getShardInfo() );
    }

    @Override
    public synchronized Long getCurrent( String prefix )
    {
        Optional<String> current = Optional.ofNullable( jedis.get( prefix ) );
        return Long.parseLong( current.orElse( "0" ) );
    }

    @Override
    public synchronized ByteArrayOutputStream generate( String prefix, Invoice invoice ) throws InvoiceServiceException
    {
        Long id = jedis.incr( prefix );

        try
        {
            return pdfService.generate( invoice.setNumber( prefix + id.toString() ) );
        }
        catch ( IOException e )
        {
            jedis.decr( prefix );
            throw new InvoiceServiceException( e );
        }
    }

    @Override
    public synchronized URI generateAndUpload( String prefix, Invoice invoice ) throws InvoiceServiceException
    {
        Long id = jedis.incr( prefix );

        try
        {
            ByteArrayOutputStream out = pdfService.generate( invoice.setNumber( prefix + id.toString() ) );
            final BlobStoreFile file = blobStoreFileFactory.produce( prefix, id );
            blobStoreService.uploadFile( out.toByteArray(), file );
            return file.getURI();
        }
        catch ( IOException e )
        {
            jedis.decr( prefix );
            throw new InvoiceServiceException( e );
        }
    }

    @Override
    public byte[] download( String prefix, Long id ) throws InvoiceServiceException
    {
        try
        {
            InputStream in = blobStoreService.downloadFile( blobStoreFileFactory.produce( prefix, id ) );
            return IOUtils.toByteArray( in );
        }
        catch ( IOException e )
        {
            throw new InvoiceServiceException( e );
        }
    }

    @Override
    public List<String> getPdfFields() throws InvoiceServiceException
    {
        try
        {
            return pdfService.getFields();
        }
        catch ( IOException e )
        {
            throw new InvoiceServiceException( e );
        }
    }
}
