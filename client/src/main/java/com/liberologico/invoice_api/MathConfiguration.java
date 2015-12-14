package com.liberologico.invoice_api;

import java.math.RoundingMode;

public class MathConfiguration
{
    /**
     * BigDecimal output precision
     */
    public final static int defaultPrecision = 2;

    /**
     * BigDecimal calculation precision
     */
    public final static int computationPrecision = 4;

    /**
     * BigDecimal roundingMode
     */
    public final static RoundingMode roundingMode = RoundingMode.HALF_EVEN;
}
