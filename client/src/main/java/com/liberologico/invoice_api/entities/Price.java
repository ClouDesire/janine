package com.liberologico.invoice_api.entities;

import com.liberologico.invoice_api.MathConfiguration;
import io.gsonfire.annotations.ExposeMethodResult;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;

public class Price
{
    @NotNull
    private BigDecimal price;

    private BigDecimal VAT = BigDecimal.ZERO;

    private String currency;

    @ExposeMethodResult( "total" )
    public BigDecimal getTotal()
    {
        final BigDecimal value = price.add( price.divide( new BigDecimal( 100 ) ).multiply( VAT ) );
        return value.setScale( MathConfiguration.computationPrecision, MathConfiguration.roundingMode );
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
        return VAT;
    }

    public Price setVAT( BigDecimal vAT )
    {
        if ( vAT.compareTo( BigDecimal.ZERO ) < 0 || vAT.compareTo( new BigDecimal( 100 ) ) > 0 )
            throw new IllegalArgumentException( "Vat is a percentage!" );
        VAT = vAT;
        return this;
    }

    public String getCurrency()
    {
        return currency;
    }

    public Price setCurrency( String currency )
    {
        this.currency = currency;
        return this;
    }

    @Override
    public String toString()
    {
        return "Price [price=" + price + ", VAT=" + VAT + "%, currency=" + currency + "]";
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        Price price = (Price) o;
        return Objects.equals( this.price, price.price ) &&
                Objects.equals( VAT, price.VAT ) &&
                Objects.equals( currency, price.currency );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( price, VAT, currency );
    }
}
