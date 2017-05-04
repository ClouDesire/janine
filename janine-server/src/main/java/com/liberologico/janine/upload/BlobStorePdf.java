package com.liberologico.janine.upload;

public class BlobStorePdf extends BlobStoreFile
{
    private static final String PATTERN = "{0}.pdf";

    public BlobStorePdf( String baseUrl, String prefix, String owner, Long id )
    {
        super( baseUrl, prefix, owner, id );
    }

    @Override
    public String getPattern()
    {
        return PATTERN;
    }
}
