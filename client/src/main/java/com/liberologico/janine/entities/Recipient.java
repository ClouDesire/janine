package com.liberologico.janine.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.liberologico.janine.validation.Identifiable;

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
        if ( companyName == null || companyName.isEmpty() )
        {
            return super.toString();
        }

        return companyName;
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
