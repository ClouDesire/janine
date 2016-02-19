package com.liberologico.janine.pdf;

import com.liberologico.janine.entities.Invoice;
import com.liberologico.janine.entities.Line;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class PdfServiceImpl implements PdfService
{
    private final static DateFormat DATE_FORMAT = new SimpleDateFormat( "dd/MM/yyyy" );

    @Autowired
    private ResourceLoader resourceLoader;

    @Value( "${template.location}" )
    private String templateLocation;

    @Override
    public List<String> getFields() throws IOException
    {
        Resource resource = resourceLoader.getResource( templateLocation );

        try ( PDDocument pdfDocument = PDDocument.load( resource.getInputStream() ) )
        {
            PDDocumentCatalog docCatalog = pdfDocument.getDocumentCatalog();
            PDAcroForm acroForm = docCatalog.getAcroForm();
            return acroForm.getFields().stream().map( PDField::getFullyQualifiedName ).collect( Collectors.toList() );
        }
    }

    @Override
    public ByteArrayOutputStream generate( Invoice invoice ) throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Resource resource = resourceLoader.getResource( templateLocation );

        try ( PDDocument pdfDocument = PDDocument.load( resource.getInputStream() ) )
        {
            PDDocumentCatalog docCatalog = pdfDocument.getDocumentCatalog();
            PDAcroForm acroForm = docCatalog.getAcroForm();
            acroForm.setCacheFields( true );

            setFields( invoice, acroForm );
            acroForm.getFieldIterator().forEachRemaining( pdField -> pdField.setReadOnly( true ) );

            AccessPermission ap = new AccessPermission();
            ap.setCanModify( false );
            ap.setReadOnly();
            StandardProtectionPolicy spp = new StandardProtectionPolicy( UUID.randomUUID().toString(), "", ap );
            spp.setEncryptionKeyLength( 128 );
            pdfDocument.protect( spp );

            pdfDocument.save( out );
        }

        return out;
    }

    private PDAcroForm setFields( Invoice invoice, PDAcroForm acroForm ) throws IOException
    {
        setField( acroForm, "header", invoice.getHeader() );
        setField( acroForm, "holderName", invoice.getHolder().getCompanyName() );
        setField( acroForm, "holderVat", "VAT: " + invoice.getHolder().getTaxCode() );
        setField( acroForm, "holderAddress1", invoice.getHolder().getAddress().toLineOne() );
        setField( acroForm, "holderAddress2", invoice.getHolder().getAddress().toLineTwo() );
        setField( acroForm, "recipientName", invoice.getRecipient().toCompanyLine() );
        setField( acroForm, "recipientAddress1", invoice.getRecipient().getAddress().toLineOne() );
        setField( acroForm, "recipientAddress2", invoice.getRecipient().getAddress().toLineTwo() );
        setField( acroForm, "recipientVat", invoice.getRecipient().getTaxCode() );
        setField( acroForm, "number", invoice.getNumber() );
        setField( acroForm, "date", DATE_FORMAT.format( invoice.getDate() ) );

        ListIterator<Line> it = invoice.getLines().listIterator();
        while ( it.hasNext() )
        {
            int itemIndex = it.nextIndex() + 1;
            Line line = it.next();
            setField( acroForm, "description" + itemIndex, line.getDescription() );
            setField( acroForm, "vat" + itemIndex, line.printVAT() );
            setField( acroForm, "quantity" + itemIndex, line.getQuantity().toPlainString() );
            setField( acroForm, "price" + itemIndex, line.printPrice( invoice.getCurrency() ) );
            setField( acroForm, "total" + itemIndex, line.printTotal( invoice.getCurrency() ) );
        }

        setField( acroForm, "subTotal", invoice.printSubTotal() );
        setField( acroForm, "vat", invoice.printVAT() );
        setField( acroForm, "vatPercentage", invoice.printVATPercentage() );
        setField( acroForm, "total", invoice.printTotal() );
        setField( acroForm, "notes", invoice.getNotes() );

        return acroForm;
    }

    private PDAcroForm setField( PDAcroForm acroForm, String name, String value ) throws IOException
    {
        PDField field = acroForm.getField( name );

        if ( field == null ) throw new RuntimeException( "No field found with name:" + name );

        field.setValue( value );
        return acroForm;
    }
}
