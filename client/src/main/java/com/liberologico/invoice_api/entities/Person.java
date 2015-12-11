package com.liberologico.invoice_api.entities;

import javax.validation.constraints.NotNull;

public class Person
{
    private String firstName;

    private String lastName;

    @NotNull
    private String email;

    private String phoneNumber;

    private Address address;

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
}
