package com.liberologico.janine.controllers;

import com.liberologico.janine.entities.Invoice;
import com.liberologico.janine.exceptions.InvoiceMissingException;
import com.liberologico.janine.exceptions.InvoiceServiceException;
import com.liberologico.janine.services.InvoiceService;
import com.liberologico.janine.upload.BlobStorePdf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.util.List;

@RestController
public class InvoiceController
{
    @Autowired
    private InvoiceService service;

    @RequestMapping( value = "/{prefix}/validate", method = RequestMethod.POST )
    Invoice validate( @PathVariable String prefix, @RequestBody @Valid Invoice invoice )
    {
        Long id = service.getCurrentId( prefix );

        return invoice.setNumber( prefix + String.valueOf( id + 1 ) );
    }

    @RequestMapping( value = "/{prefix}/download", method = RequestMethod.POST )
    ResponseEntity<byte[]> generate( @PathVariable String prefix, @RequestBody @Valid Invoice invoice )
            throws InvoiceServiceException
    {
        ByteArrayOutputStream out = service.generate( prefix, invoice );

        return new ResponseEntity<>( out.toByteArray(), HttpStatus.OK );
    }

    @RequestMapping( value = "/{prefix}", method = RequestMethod.POST )
    ResponseEntity<Long> generateAndUpload( @PathVariable String prefix, @RequestBody @Valid Invoice invoice )
            throws InvoiceServiceException
    {
        BlobStorePdf pdf = service.generateAndUpload( prefix, invoice );

        return ResponseEntity.created( pdf.getURI() ).body( pdf.getId() );
    }

    @RequestMapping( value = "/{prefix}/{id:\\d+}.{format:pdf|json}", method = RequestMethod.GET )
    ResponseEntity<byte[]> download( @PathVariable String prefix, @PathVariable Long id, @PathVariable String format )
            throws InvoiceServiceException, InvoiceMissingException
    {
        byte[] file = service.download( prefix, id, format );

        return new ResponseEntity<>( file, HttpStatus.OK );
    }

    @RequestMapping( value  = "/fields", method = RequestMethod.GET )
    ResponseEntity<List<String>> getFields() throws InvoiceServiceException
    {
        return new ResponseEntity<>( service.getPdfFields(), HttpStatus.OK );
    }
}
