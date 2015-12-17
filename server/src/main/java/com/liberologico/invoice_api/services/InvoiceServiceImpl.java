package com.liberologico.invoice_api.services;

import com.liberologico.invoice_api.entities.Invoice;
import com.liberologico.invoice_api.exceptions.InvoiceServiceException;
import com.liberologico.invoice_api.pdf.PdfService;
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
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.List;

@Component
public class InvoiceServiceImpl implements InvoiceService
{
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
        return Long.parseLong( jedis.get( prefix ) );
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
            URL url = blobStoreService.uploadFile( out.toByteArray(), id, prefix );
            return url.toURI();
        }
        catch ( IOException | URISyntaxException e )
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
            InputStream in = blobStoreService.downloadFile( MessageFormat.format( "{0}.pdf", id ), prefix );
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
