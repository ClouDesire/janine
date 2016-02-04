package com.liberologico.janine.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.liberologico.janine.validation.Identifiable;

import java.text.MessageFormat;

@Identifiable
public class Recipient extends Person
{
    @JsonInclude( JsonInclude.Include.NON_NULL )
    private String taxCode;

    @JsonInclude( JsonInclude.Include.NON_NULL )
    private String companyName;

    @Override
    public String toCompanyLine()
    {
        if ( ( taxCode == null || taxCode.isEmpty() ) && ( companyName == null || companyName.isEmpty() ) )
        {
            return super.toString();
        }

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
