package com.liberologico.janine.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import java.text.MessageFormat;

public class Holder extends Person
{
    @JsonProperty( required = true )
    @NotEmpty
    private String taxCode;

    @JsonProperty( required = true )
    @NotEmpty
    private String companyName;

    @Override
    public String toCompanyLine()
    {
        return MessageFormat.format( "{0} - {1}", companyName, taxCode );
    }

    @Override
    public String getTaxCode()
    {
        return taxCode;
    }

    @Override
    public Holder setTaxCode( String taxCode )
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
    public Holder setCompanyName( String companyName )
    {
        this.companyName = companyName;
        return this;
    }
}
