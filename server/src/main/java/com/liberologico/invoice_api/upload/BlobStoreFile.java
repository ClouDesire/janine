package com.liberologico.invoice_api.upload;

import java.text.MessageFormat;

public class BlobStoreFile
{
    public static final String PATTERN = "{0}.pdf";

    private String owner;

    private Long id;

    public BlobStoreFile( String owner, Long id )
    {
        this.owner = owner;
        this.id = id;
    }

    public String getFilename()
    {
        return MessageFormat.format( PATTERN, id );
    }

    public String getContainer( String prefix )
    {
        return prefix + owner;
    }
}
