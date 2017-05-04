package com.liberologico.janine.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import java.text.MessageFormat;

abstract public class Person
{
    @JsonInclude( JsonInclude.Include.NON_NULL )
    private String firstName;

    @JsonInclude( JsonInclude.Include.NON_NULL )
    private String lastName;

    @Email
    @JsonInclude( JsonInclude.Include.NON_NULL )
    private String email;

    @JsonInclude( JsonInclude.Include.NON_NULL )
    private String phoneNumber;

    @JsonProperty( required = true )
    @NotNull
    private Address address;

    abstract public String getTaxCode();
    abstract public Person setTaxCode( String taxCode );

    abstract public String getCompanyName();
    abstract public Person setCompanyName( String companyName );

    abstract public String toCompanyLine();

    @Override
    public String toString()
    {
        if ( email == null || email.isEmpty() ) return MessageFormat.format( "{0} {1}", firstName, lastName );

        return MessageFormat.format( "{0} {1} <{2}>", firstName, lastName, email );
    }

    public String getFirstName()
    {
        return firstName;
    }

    public Person setFirstName( String firstName )
    {
        this.firstName = firstName;
        return this;
    }

    public String getLastName()
    {
        return lastName;
    }

    public Person setLastName( String lastName )
    {
        this.lastName = lastName;
        return this;
    }

    public String getEmail()
    {
        return email;
    }

    public Person setEmail( String email )
    {
        this.email = email;
        return this;
    }

    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public Person setPhoneNumber( String phoneNumber )
    {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public Address getAddress()
    {
        return address;
    }

    public Person setAddress( Address address )
    {
        this.address = address;
        return this;
    }
}
