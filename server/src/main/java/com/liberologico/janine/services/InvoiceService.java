package com.liberologico.janine.services;

import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.liberologico.janine.entities.Invoice;
import com.liberologico.janine.exceptions.InvoiceServiceException;
import com.liberologico.janine.upload.BlobStorePdf;

import java.io.ByteArrayOutputStream;
import java.util.List;

public interface InvoiceService
{
    /**
     * Retrieve the current value of the specified counter
     * @param prefix the namespace that identifies a particular counter
     * @return the current value of the specified counter
     */
    Long getCurrentId( String prefix );

    /**
     * Generates a PDF for the given invoice
     * @param prefix the namespace of this invoice
     * @param invoice object holding all the invoice information
     * @return A stream representing the PDF bytes
     * @throws InvoiceServiceException if problems occurred during PDF generation
     */
    ByteArrayOutputStream generate( String prefix, Invoice invoice ) throws InvoiceServiceException;

    /**
     * Generates a PDF for the given invoice, and upload it to a remote object storage
     * @param prefix the namespace of this invoice
     * @param invoice object holding all the invoice information
     * @return An object holding the reference to the uploaded object
     * @throws InvoiceServiceException if problems occurred during PDF generation or object upload
     */
    BlobStorePdf generateAndUpload( String prefix, Invoice invoice ) throws InvoiceServiceException;

    /**
     * Generates a PDF for the given invoice and upload it to a remote
     * object storage providing your own id
     *
     * @param prefix     the namespace of this invoice
     * @param id         the provided identifier of the invoice
     * @param invoice    object holding all the invoice information
     * @param regenerate whether to override an eventually already generated invoice
     *
     * @return An object holding the reference to the uploaded object
     *
     * @throws InvoiceServiceException if problems occurred during PDF generation or object upload
     */
    BlobStorePdf generateAndUpload( String prefix, Long id, Invoice invoice, boolean regenerate )
            throws InvoiceServiceException;

    /**
     * Downloads an already generated invoice
     * @param prefix the namespace of the invoice
     * @param id the identifier of the invoice
     * @param format pdf or json output
     * @return the binary payload of the requested invoice in the requested format
     * @throws InvoiceServiceException if problems occurred while retrieving object
     */
    byte[] download( String prefix, Long id, String format ) throws InvoiceServiceException;

    /**
     * List fields of the current PDF template - useful for debug
     * @return a list of fields available in the template
     * @throws InvoiceServiceException if problems occurred while processing the PDF template
     */
    List<String> getPdfFields() throws InvoiceServiceException;

    /**
     * Get a JsonSchema representing the Invoice object
     * @return Constructed JSON schema.
     * @throws InvoiceServiceException
     */
    JsonSchema getInvoiceSchema() throws InvoiceServiceException;
}
