package com.liberologico.invoice_api.entities;

import com.google.gson.annotations.Expose;
import io.gsonfire.annotations.ExposeMethodResult;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Invoice
{
    @Expose( deserialize = false )
    private String number;

    @Expose( deserialize = false )
    private Date date = new Date();

    @NotNull
    @Valid
    private Person holder;

    @NotNull
    @Valid
    private Person recipient;

    @ExposeMethodResult( "total" )
    public BigDecimal getTotal()
    {
        return lines.stream().map( Line::calculateTotalPrice ).reduce( BigDecimal.ZERO, BigDecimal::add );
    }

    public String getNumber()
    {
        return number;
    }

    public Invoice setNumber( String number )
    {
        this.number = number;
        return this;
    }

    public Date getDate()
    {
        return date;
    }

    public Invoice setDate( Date date )
    {
        this.date = date;
        return this;
    }

    @NotEmpty
    @Valid
    private List<Line> lines = new ArrayList<>();

    public List<Line> getLines()
    {
        return lines;
    }

    public Invoice setLines( List<Line> lines )
    {
        this.lines = lines;
        return this;
    }

    public Person getHolder()
    {
        return holder;
    }

    public Invoice setHolder( Person holder )
    {
        this.holder = holder;
        return this;
    }

    public Person getRecipient()
    {
        return recipient;
    }

    public Invoice setRecipient( Person recipient )
    {
        this.recipient = recipient;
        return this;
    }
}
