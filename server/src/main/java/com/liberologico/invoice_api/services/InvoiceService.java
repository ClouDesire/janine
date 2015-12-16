package com.liberologico.invoice_api.services;

import com.liberologico.invoice_api.entities.Invoice;
import com.liberologico.invoice_api.exceptions.InvoiceServiceException;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.List;

public interface InvoiceService
{
    Long getCurrent( String prefix );

    ByteArrayOutputStream generate( String prefix, Invoice invoice ) throws InvoiceServiceException;

    URL generateAndUpload( String prefix, Invoice invoice ) throws InvoiceServiceException;

    List<String> getPdfFields() throws InvoiceServiceException;
}
