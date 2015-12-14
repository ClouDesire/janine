package com.liberologico.invoice_api.entities;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class Invoice
{
    @NotNull
    @Valid
    private Person holder;

    @NotNull
    @Valid
    private Person recipient;

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
