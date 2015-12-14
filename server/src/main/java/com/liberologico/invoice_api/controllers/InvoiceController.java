package com.liberologico.invoice_api.controllers;

import com.liberologico.invoice_api.entities.Invoice;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class InvoiceController
{
    @RequestMapping( value = "/", method = RequestMethod.POST, consumes = "application/json" )
    Invoice post( @RequestBody @Valid Invoice invoice )
    {
        return invoice;
    }
}
