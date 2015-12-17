package com.liberologico.invoice_api.controllers;

import com.liberologico.invoice_api.entities.Invoice;
import com.liberologico.invoice_api.exceptions.InvoiceServiceException;
import com.liberologico.invoice_api.services.InvoiceService;
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
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

@RestController
public class InvoiceController
{
    @Autowired
    private InvoiceService service;

    @RequestMapping( value = "/{prefix}/validate", method = RequestMethod.POST )
    Invoice validate( @PathVariable String prefix, @RequestBody @Valid Invoice invoice )
    {
        Long id = service.getCurrent( prefix );

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
    ResponseEntity<Void> generateAndUpload( @PathVariable String prefix, @RequestBody @Valid Invoice invoice )
            throws InvoiceServiceException, URISyntaxException
    {
        URL url = service.generateAndUpload( prefix, invoice );

        return ResponseEntity.created( url.toURI() ).build();
    }

    @RequestMapping( value  = "/fields", method = RequestMethod.GET )
    ResponseEntity<List<String>> getFields() throws InvoiceServiceException
    {
        return new ResponseEntity<>( service.getPdfFields(), HttpStatus.OK );
    }
}
