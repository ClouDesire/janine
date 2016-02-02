package com.liberologico.janine.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.liberologico.janine.MathConfiguration;
import io.gsonfire.annotations.ExposeMethodResult;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;

public class Line
{
    @JsonProperty( required = true )
    @NotNull
    private Price price;

    @DecimalMin( "0.01" )
    @JsonProperty( required = true )
    @NotNull
    private BigDecimal quantity = BigDecimal.ONE;

    @JsonProperty( required = true )
    @NotNull
    private String unit = "un";

    @JsonProperty( required = true )
    @Length( max = 1024 )
    private String description;

    @ExposeMethodResult( "totalPrice" )
    public BigDecimal calculateTotalPrice()
    {
        if ( price == null ) return BigDecimal.ZERO;
        return price.getTotal().multiply( quantity )
                .setScale( MathConfiguration.defaultPrecision, MathConfiguration.roundingMode );
    }

    public String getDescription()
    {
        return description;
    }

    public Line setDescription( String description )
    {
        this.description = description;
        return this;
    }

    public Price getPrice()
    {
        return price;
    }

    public Line setPrice( Price price )
    {
        this.price = price;
        return this;
    }

    public BigDecimal getQuantity()
    {
        return quantity;
    }

    public Line setQuantity( BigDecimal quantity )
    {
        this.quantity = quantity.setScale( MathConfiguration.computationPrecision, MathConfiguration.roundingMode );
        return this;
    }

    public String getUnit()
    {
        return unit;
    }

    public Line setUnit( String unit )
    {
        this.unit = unit;
        return this;
    }

    public String printPrice( String currency )
    {
        if ( currency == null ) throw new IllegalArgumentException( "please provide a currency" );
        assert price != null;
        return currency + ' ' + price.getPrice().toPlainString();
    }

    public String printTotal( String currency )
    {
        if ( currency == null ) throw new IllegalArgumentException( "please provide a currency" );
        return currency + ' ' + calculateTotalPrice().toPlainString();
    }

    public String printVAT()
    {
        assert price != null;
        return price.getVAT().toPlainString() + '%';
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        Line that = (Line) o;
        return Objects.equals( price, that.price ) &&
                Objects.equals( quantity, that.quantity ) &&
                Objects.equals( unit, that.unit ) &&
                Objects.equals( description, that.description );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( price, quantity, unit, description );
    }

    @Override
    public String toString()
    {
        return "Line [price=" + price + ", quantity=" + quantity + ", unit=" + unit + ", description=" + description
                + ", total=" + calculateTotalPrice() + "]";
    }
}
