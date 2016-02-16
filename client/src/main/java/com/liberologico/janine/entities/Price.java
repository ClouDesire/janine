package com.liberologico.janine.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.liberologico.janine.MathConfiguration;
import io.gsonfire.annotations.ExposeMethodResult;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;

public class Price
{
    @JsonProperty( required = true )
    @NotNull
    private BigDecimal price;

    @DecimalMax( "99.99" )
    @DecimalMin( "0.00" )
    @JsonProperty( required = true )
    @NotNull
    private BigDecimal VAT = BigDecimal.ZERO;

    @ExposeMethodResult( "total" )
    public BigDecimal getTotal()
    {
        final BigDecimal percentage = MathConfiguration.calculatePercentage( price, VAT );

        return price.add( percentage )
                    .setScale( MathConfiguration.computationPrecision, MathConfiguration.roundingMode );
    }

    @ExposeMethodResult( "vat_total" )
    public BigDecimal getVATTotal()
    {
        return  MathConfiguration.calculatePercentage( price, VAT );
    }

    public BigDecimal getPrice()
    {
        return price.setScale( MathConfiguration.computationPrecision, MathConfiguration.roundingMode );
    }

    public Price setPrice( BigDecimal price )
    {
        this.price = price;
        return this;
    }

    public BigDecimal getVAT()
    {
        return VAT.stripTrailingZeros();
    }

    public Price setVAT( BigDecimal VAT )
    {
        if ( VAT.compareTo( BigDecimal.ZERO ) < 0 || VAT.compareTo( MathConfiguration.ONE_HUNDRED ) > 0 )
        {
            throw new IllegalArgumentException( "VAT should be a percentage between 0.00 and 99.99" );
        }

        this.VAT = VAT;
        return this;
    }

    @Override
    public String toString()
    {
        return "Price [price=" + price + ", VAT=" + VAT + "%]";
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        Price price = (Price) o;
        return Objects.equals( this.price, price.price ) &&
                Objects.equals( VAT, price.VAT );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( price, VAT );
    }
}
