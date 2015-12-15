package com.liberologico.invoice_api.pdf;

import com.liberologico.invoice_api.entities.Invoice;

import java.io.ByteArrayOutputStream;
import java.util.List;

public interface PdfService
{
    List<String> getFields();

    ByteArrayOutputStream generate( Invoice invoice );
}
