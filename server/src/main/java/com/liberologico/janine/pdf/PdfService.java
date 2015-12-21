package com.liberologico.janine.pdf;

import com.liberologico.janine.entities.Invoice;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public interface PdfService
{
    List<String> getFields() throws IOException;

    ByteArrayOutputStream generate( Invoice invoice ) throws IOException;
}
