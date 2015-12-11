package com.liberologico.invoice_api.controllers;

import com.liberologico.invoice_api.entities.Invoice;
import com.liberologico.invoice_api.entities.Line;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.stream.Collectors;

@RestController
public class InvoiceController
{
    @RequestMapping( value = "/", method = RequestMethod.POST, consumes = "application/json" )
    String post( @RequestBody @Valid Invoice invoice )
    {
        return invoice.getLines().stream()
                .map( Line::toString )
                .collect( Collectors.joining( "\n" ) );
    }
}
