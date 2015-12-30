package com.liberologico.janine.entities;

public class Recipient extends Person
{
    private String taxCode;

    private String companyName;

    @Override
    public String getTaxCode()
    {
        return taxCode;
    }

    @Override
    public Recipient setTaxCode( String taxCode )
    {
        this.taxCode = taxCode;
        return this;
    }

    @Override
    public String getCompanyName()
    {
        return companyName;
    }

    @Override
    public Recipient setCompanyName( String companyName )
    {
        this.companyName = companyName;
        return this;
    }
}
