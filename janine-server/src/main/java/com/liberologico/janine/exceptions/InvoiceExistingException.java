package com.liberologico.janine.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus( HttpStatus.CONFLICT )
public class InvoiceExistingException extends InvoiceServiceException
{
    public InvoiceExistingException( String message )
    {
        super( message );
    }
}
