package com.liberologico.janine.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;
import io.gsonfire.annotations.ExposeMethodResult;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Invoice
{
    @Expose( deserialize = false )
    private String number;

    private Date date = new Date();

    @JsonProperty( required = true )
    @Length( max = 64 )
    private String header;

    @JsonProperty( required = true )
    @Length( max = 1024 )
    private String notes;

    @JsonProperty( required = true )
    @NotNull
    @Valid
    private Holder holder;

    @JsonProperty( required = true )
    @NotNull
    @Valid
    private Recipient recipient;

    @JsonProperty( required = true )
    @Pattern( regexp = "[\\w]{3}" )
    private String currency;

    @JsonProperty( required = true )
    @NotEmpty
    @Valid
    private List<Line> lines = new ArrayList<>();

    @ExposeMethodResult( "total" )
    public BigDecimal getTotal()
    {
        BigDecimal total = BigDecimal.ZERO;
        for ( Line line : lines )
        {
            total = total.add( line.calculateTotalPrice() );
        }
        return total;
    }

    public String getCurrency()
    {
        return currency.toUpperCase();
    }

    public Invoice setCurrency( String currency )
    {
        this.currency = currency;
        return this;
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

    public String getHeader()
    {
        return header;
    }

    public void setHeader( String header )
    {
        this.header = header;
    }

    public String getNotes()
    {
        return notes;
    }

    public void setNotes( String notes )
    {
        this.notes = notes;
    }

    public List<Line> getLines()
    {
        return lines;
    }

    public Invoice setLines( List<Line> lines )
    {
        this.lines = lines;
        return this;
    }

    public Holder getHolder()
    {
        return holder;
    }

    public Invoice setHolder( Holder holder )
    {
        this.holder = holder;
        return this;
    }

    public Recipient getRecipient()
    {
        return recipient;
    }

    public Invoice setRecipient( Recipient recipient )
    {
        this.recipient = recipient;
        return this;
    }
}
