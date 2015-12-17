package com.liberologico.invoice_api.services;

import com.liberologico.invoice_api.entities.Invoice;
import com.liberologico.invoice_api.exceptions.InvoiceServiceException;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.List;

public interface InvoiceService
{
    Long getCurrent( String prefix );

    ByteArrayOutputStream generate( String prefix, Invoice invoice ) throws InvoiceServiceException;

    URI generateAndUpload( String prefix, Invoice invoice ) throws InvoiceServiceException;

    byte[] download( String prefix, Long id ) throws InvoiceServiceException;

    List<String> getPdfFields() throws InvoiceServiceException;
}
