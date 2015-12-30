package com.liberologico.janine.entities;

import org.hibernate.validator.constraints.NotEmpty;

public class Holder extends Person
{
    @NotEmpty
    private String taxCode;

    @NotEmpty
    private String companyName;

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
