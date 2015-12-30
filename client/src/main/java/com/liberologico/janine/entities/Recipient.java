package com.liberologico.janine.entities;

import java.text.MessageFormat;

public class Recipient extends Person
{
    private String taxCode;

    private String companyName;

    @Override
    public String getCompanyLine()
    {
        if ( ( taxCode == null || taxCode.isEmpty() ) && ( companyName == null || companyName.isEmpty() ) ) return "";
        if ( taxCode == null || taxCode.isEmpty() ) return companyName;
        if ( companyName == null || companyName.isEmpty() ) return taxCode;
        return MessageFormat.format( "{0} - {1}", companyName, taxCode );
    }

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
