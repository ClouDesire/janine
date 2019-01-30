package com.liberologico.janine.controllers;

import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.liberologico.janine.entities.Invoice;
import com.liberologico.janine.exceptions.InvoiceServiceException;
import com.liberologico.janine.services.InvoiceService;
import com.liberologico.janine.upload.BlobStorePdf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.util.List;

@RestController
public class InvoiceController
{
    private final InvoiceService service;

    @Autowired
    public InvoiceController( InvoiceService service )
    {
        this.service = service;
    }

    @PostMapping( "/{prefix}/validate" )
    Invoice validate( @PathVariable String prefix, @RequestBody @Valid Invoice invoice )
    {
        Long id = service.getCurrentId( prefix );

        return invoice.setNumber( prefix + ( id + 1 ) );
    }

    @PostMapping( "/{prefix}/download" )
    ResponseEntity<byte[]> generate( @PathVariable String prefix, @RequestBody @Valid Invoice invoice )
            throws InvoiceServiceException
    {
        ByteArrayOutputStream out = service.generate( prefix, invoice );

        return new ResponseEntity<>( out.toByteArray(), HttpStatus.OK );
    }

    @PostMapping( "/{prefix}/{id}/download" )
    ResponseEntity<byte[]> generateWithId( @PathVariable String prefix, @PathVariable Long id,
            @RequestBody @Valid Invoice invoice ) throws InvoiceServiceException
    {
        ByteArrayOutputStream out = service.generate( prefix, id, invoice );

        return new ResponseEntity<>( out.toByteArray(), HttpStatus.OK );
    }

    @PostMapping( "/{prefix}" )
    ResponseEntity<Long> generateAndUpload( @PathVariable String prefix, @RequestBody @Valid Invoice invoice )
            throws InvoiceServiceException
    {
        BlobStorePdf pdf = service.generateAndUpload( prefix, invoice );

        return ResponseEntity.created( pdf.getURI() ).body( pdf.getId() );
    }

    @PostMapping( "/{prefix}/{id}" )
    ResponseEntity<Long> generateAndUploadWithId( @PathVariable String prefix, @PathVariable Long id,
            @RequestParam( defaultValue = "false" ) boolean regenerate, @RequestBody @Valid Invoice invoice )
            throws InvoiceServiceException
    {
        BlobStorePdf pdf = service.generateAndUpload( prefix, id, invoice, regenerate );

        return ResponseEntity.created( pdf.getURI() ).body( pdf.getId() );
    }

    @GetMapping( "/{prefix}/{id:\\d+}.{format:pdf|json}" )
    ResponseEntity<byte[]> download( @PathVariable String prefix, @PathVariable Long id, @PathVariable String format )
            throws InvoiceServiceException
    {
        byte[] file = service.download( prefix, id, format );

        return new ResponseEntity<>( file, HttpStatus.OK );
    }

    @GetMapping( "/fields" )
    ResponseEntity<List<String>> getFields() throws InvoiceServiceException
    {
        return new ResponseEntity<>( service.getPdfFields(), HttpStatus.OK );
    }

    @GetMapping( "/schema" )
    ResponseEntity<JsonSchema> getSchema() throws InvoiceServiceException
    {
        return new ResponseEntity<>( service.getInvoiceSchema(), HttpStatus.OK );
    }
}
