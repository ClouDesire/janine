package com.liberologico.invoice_api.entities;

public class Address
{
    private String address;
    private String city;
    private String country;
    private String state;
    private String zip;

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
}
