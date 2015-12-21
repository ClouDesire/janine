package com.liberologico.janine.upload;

import java.net.URI;
import java.text.MessageFormat;
import java.util.Objects;

public abstract class BlobStoreFile
{
    private String baseUrl;

    private String prefix;

    private String owner;

    private Long id;

    public BlobStoreFile( String baseUrl, String prefix, String owner, Long id )
    {
        this.baseUrl = baseUrl;
        this.prefix = prefix;
        this.owner = owner;
        this.id = id;
    }

    abstract String getPattern();

    public String getFilename()
    {
        return MessageFormat.format( getPattern(), id );
    }

    public String getContainer()
    {
        return prefix + owner;
    }

    public URI getURI()
    {
        return URI.create( getBaseUrl() + owner + "/" + getFilename() );
    }

    private String getBaseUrl()
    {
        String url = baseUrl;
        if ( url.charAt( baseUrl.length() - 1 ) != '/' ) url += '/';
        return url;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        BlobStoreFile that = (BlobStoreFile) o;
        return Objects.equals( baseUrl, that.baseUrl ) &&
                Objects.equals( prefix, that.prefix ) &&
                Objects.equals( owner, that.owner ) &&
                Objects.equals( id, that.id );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( baseUrl, prefix, owner, id );
    }
}
