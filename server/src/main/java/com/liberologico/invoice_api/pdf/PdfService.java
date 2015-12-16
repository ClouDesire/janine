package com.liberologico.invoice_api.pdf;

import com.liberologico.invoice_api.entities.Invoice;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public interface PdfService
{
    List<String> getFields() throws IOException;

    ByteArrayOutputStream generate( Invoice invoice ) throws IOException;
}
