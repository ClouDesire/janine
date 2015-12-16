package com.liberologico.invoice_api.exceptions;

public class InvoiceServiceException extends Exception
{
    public InvoiceServiceException( Exception e )
    {
        super( e );
    }
}
