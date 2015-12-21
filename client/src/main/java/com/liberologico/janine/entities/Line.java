package com.liberologico.janine.entities;

import com.liberologico.janine.MathConfiguration;
import io.gsonfire.annotations.ExposeMethodResult;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;

public class Line
{
    @NotNull
    private Price price;

    @DecimalMin( "0.01" )
    @NotNull
    private BigDecimal quantity = BigDecimal.ONE;

    @NotNull
    private String unit = "un";

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
