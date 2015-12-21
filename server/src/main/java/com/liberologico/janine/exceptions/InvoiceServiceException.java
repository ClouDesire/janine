package com.liberologico.janine.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus( HttpStatus.INTERNAL_SERVER_ERROR )
public class InvoiceServiceException extends Exception
{
    private final static Logger log = LoggerFactory.getLogger( InvoiceServiceException.class );

    public InvoiceServiceException( Exception e )
    {
        super( e );
        log.error( e.getMessage() );
    }
}
