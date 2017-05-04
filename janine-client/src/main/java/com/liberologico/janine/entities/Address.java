package com.liberologico.janine.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import java.text.MessageFormat;

public class Address
{
    @JsonProperty( required = true )
    @NotNull
    private String address;
    @JsonProperty( required = true )
    @NotNull
    private String city;
    @JsonProperty( required = true )
    @NotNull
    private String country;
    @JsonProperty( required = true )
    @NotNull
    private String state;
    @JsonProperty( required = true )
    @NotNull
    private String zip;

    public Address( String address, String city, String country, String state, String zip )
    {
        this.address = address;
        this.city = city;
        this.country = country;
        this.state = state;
        this.zip = zip;
    }

    public String toShortLine()
    {
        return MessageFormat.format( "{0} - {1} ({2})", address, city, country );
    }

    public String toLineOne()
    {
        return MessageFormat.format( "{0} - {1}", address, city );
    }

    public String toLineTwo()
    {
        return MessageFormat.format( "{0} {1} ({2})", zip, state, country );
    }

    // region Auto-generated code
    public Address()
    {
    }

    public String getAddress()
    {
        return address;
    }

    public Address setAddress( String address )
    {
        this.address = address;
        return this;
    }

    public String getCity()
    {
        return city;
    }

    public Address setCity( String city )
    {
        this.city = city;
        return this;
    }

    public String getCountry()
    {
        return country;
    }

    public Address setCountry( String country )
    {
        this.country = country;
        return this;
    }

    public String getState()
    {
        return state;
    }

    public Address setState( String state )
    {
        this.state = state;
        return this;
    }

    public String getZip()
    {
        return zip;
    }

    public Address setZip( String zip )
    {
        this.zip = zip;
        return this;
    }
    // endregion
}
