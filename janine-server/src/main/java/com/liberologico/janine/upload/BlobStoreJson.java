package com.liberologico.janine.upload;

public class BlobStoreJson extends BlobStoreFile
{
    private static final String PATTERN = "{0}.json";

    public BlobStoreJson( String baseUrl, String prefix, String owner, Long id )
    {
        super( baseUrl, prefix, owner, id );
    }

    @Override
    public String getPattern()
    {
        return PATTERN;
    }
}
