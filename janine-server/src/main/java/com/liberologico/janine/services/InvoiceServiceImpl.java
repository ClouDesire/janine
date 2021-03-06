package com.liberologico.janine.services;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.customProperties.ValidationSchemaFactoryWrapper;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;
import com.liberologico.janine.entities.Invoice;
import com.liberologico.janine.exceptions.InvoiceExistingException;
import com.liberologico.janine.exceptions.InvoiceMissingException;
import com.liberologico.janine.exceptions.InvoiceServiceException;
import com.liberologico.janine.pdf.PdfService;
import com.liberologico.janine.upload.BlobStoreFileFactory;
import com.liberologico.janine.upload.BlobStoreJson;
import com.liberologico.janine.upload.BlobStorePdf;
import com.liberologico.janine.upload.StoreService;
import org.apache.pdfbox.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

@Component
public class InvoiceServiceImpl implements InvoiceService
{
    private final BlobStoreFileFactory blobStoreFileFactory;

    private final StoreService storeService;

    private final ObjectMapper objectMapper;

    private final PdfService pdfService;

    private final JedisPool jedisPool;

    @Value( "${app.alwaysRegenerate}" )
    private boolean alwaysRegenerate;

    @Autowired
    public InvoiceServiceImpl( BlobStoreFileFactory blobStoreFileFactory, StoreService storeService,
            ObjectMapper objectMapper, PdfService pdfService, JedisPool jedisPool )
    {
        this.blobStoreFileFactory = blobStoreFileFactory;
        this.storeService = storeService;
        this.objectMapper = objectMapper;
        this.pdfService = pdfService;
        this.jedisPool = jedisPool;
    }

    @Override
    public Long getCurrentId( String prefix )
    {
        try ( Jedis jedis = jedisPool.getResource() )
        {
            Optional<String> current = Optional.ofNullable( jedis.get( prefix ) );
            return Long.parseLong( current.orElse( "0" ) );
        }
    }

    @Override
    public synchronized ByteArrayOutputStream generate( String prefix, Invoice invoice ) throws InvoiceServiceException
    {
        try ( Jedis jedis = jedisPool.getResource() )
        {
            Long id = jedis.incr( prefix );
            try
            {
                return generate( prefix, id, invoice );
            }
            catch ( InvoiceServiceException e )
            {
                jedis.decr( prefix );
                throw e;
            }
        }
    }

    @Override
    public synchronized ByteArrayOutputStream generate( String prefix, Long id, Invoice invoice ) throws InvoiceServiceException
    {
        try
        {
            return pdfService.generate( invoice.setNumber( prefix + id.toString() ) );
        }
        catch ( IOException e )
        {
            throw new InvoiceServiceException( e );
        }
    }

    @Override
    public synchronized BlobStorePdf generateAndUpload( String prefix, Invoice invoice ) throws InvoiceServiceException
    {
        try ( Jedis jedis = jedisPool.getResource() )
        {
            Long id = jedis.incr( prefix );

            try
            {
                return generateAndUpload( prefix, id, invoice, false );
            }
            catch ( InvoiceServiceException e )
            {
                jedis.decr( prefix );
                throw e;
            }
        }
    }

    @Override
    public synchronized BlobStorePdf generateAndUpload( String prefix, Long id, Invoice invoice, boolean regenerate )
            throws InvoiceServiceException
    {
        if ( alwaysRegenerate ) regenerate = true;

        try
        {
            final BlobStorePdf pdf = blobStoreFileFactory.producePdf( prefix, id );

            if ( ! regenerate && storeService.exists( pdf ) )
            {
                String message = MessageFormat.format( "Invoice {0}{1} already exists on {2}", prefix, id, pdf.getContainer() );
                throw new InvoiceExistingException( message );
            }

            ByteArrayOutputStream out = pdfService.generate( invoice.setNumber( prefix + id.toString() ) );
            storeService.uploadFile( out.toByteArray(), pdf );

            final BlobStoreJson json = blobStoreFileFactory.produceJson( prefix, id );
            storeService.uploadFile( objectMapper.writeValueAsBytes( invoice ), json );

            return pdf;
        }
        catch ( IOException e )
        {
            throw new InvoiceServiceException( e );
        }
    }

    @Override
    public byte[] download( String prefix, Long id, String format ) throws InvoiceServiceException
    {
        try
        {
            InputStream in = storeService.downloadFile( blobStoreFileFactory.produce( format, prefix, id ) );

            if ( in == null ) throw new InvoiceMissingException();

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

    @Override
    public JsonSchema getInvoiceSchema() throws InvoiceServiceException
    {
        try
        {
            SchemaFactoryWrapper visitor = new ValidationSchemaFactoryWrapper();
            objectMapper.acceptJsonFormatVisitor( objectMapper.constructType( Invoice.class ), visitor );
            return visitor.finalSchema();
        }
        catch ( JsonMappingException e )
        {
            throw new InvoiceServiceException( e );
        }
    }
}
