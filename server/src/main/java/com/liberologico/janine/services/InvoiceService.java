package com.liberologico.janine.services;

import com.liberologico.janine.entities.Invoice;
import com.liberologico.janine.exceptions.InvoiceServiceException;
import com.liberologico.janine.upload.BlobStorePdf;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.List;

public interface InvoiceService
{
    Long getCurrent( String prefix );

    ByteArrayOutputStream generate( String prefix, Invoice invoice ) throws InvoiceServiceException;

    BlobStorePdf generateAndUpload( String prefix, Invoice invoice ) throws InvoiceServiceException;

    byte[] download( String prefix, Long id, String format ) throws InvoiceServiceException;

    List<String> getPdfFields() throws InvoiceServiceException;
}
