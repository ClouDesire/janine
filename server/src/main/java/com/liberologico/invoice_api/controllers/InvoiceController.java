package com.liberologico.invoice_api.controllers;

import com.liberologico.invoice_api.entities.Invoice;
import com.liberologico.invoice_api.pdf.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.util.List;

@RestController
public class InvoiceController
{
    private final Jedis jedis;

    @Autowired
    private PdfService pdfService;

    @Autowired
    public InvoiceController( JedisConnectionFactory connectionFactory )
    {
        this.jedis = new Jedis( connectionFactory.getShardInfo() );
    }

    @RequestMapping( value = "/{prefix}/validate", method = RequestMethod.POST )
    Invoice validate( @PathVariable String prefix, @RequestBody @Valid Invoice invoice )
    {
        Long id = Long.parseLong( jedis.get( prefix ) );

        return invoice.setNumber( prefix + String.valueOf( id + 1 ) );
    }

    @RequestMapping( value = "/{prefix}", method = RequestMethod.POST )
    ResponseEntity<byte[]> generate( @PathVariable String prefix, @RequestBody @Valid Invoice invoice )
    {
        Long id = jedis.incr( prefix );

        ByteArrayOutputStream out = pdfService.generate( invoice.setNumber( prefix + id.toString() ) );

        return new ResponseEntity<>( out.toByteArray(), HttpStatus.OK );
    }

    @RequestMapping( value  = "/fields", method = RequestMethod.GET )
    List<String> getFields()
    {
        return pdfService.getFields();
    }
}
