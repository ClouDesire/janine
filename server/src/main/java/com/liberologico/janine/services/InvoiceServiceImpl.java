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
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

@Component
public class InvoiceServiceImpl implements InvoiceService
{
    @Autowired
    private BlobStoreFileFactory blobStoreFileFactory;

    @Autowired
    private StoreService storeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PdfService pdfService;

    private final Jedis jedis;

    @Autowired
    public InvoiceServiceImpl( JedisConnectionFactory connectionFactory )
    {
        this.jedis = new Jedis( connectionFactory.getShardInfo() );
    }

    @Override
    public synchronized Long getCurrentId( String prefix )
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
    public synchronized BlobStorePdf generateAndUpload( String prefix, Invoice invoice ) throws InvoiceServiceException
    {
        Long id = jedis.incr( prefix );

        try
        {

            final BlobStorePdf pdf = blobStoreFileFactory.producePdf( prefix, id );

            if ( storeService.exists( pdf ) )
            {
                String message = MessageFormat.format( "Invoice {0}{1} already exists", prefix, id );
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
            jedis.decr( prefix );
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
