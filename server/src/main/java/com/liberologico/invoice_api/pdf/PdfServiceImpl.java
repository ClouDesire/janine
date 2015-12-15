package com.liberologico.invoice_api.pdf;

import com.liberologico.invoice_api.entities.Invoice;
import com.liberologico.invoice_api.entities.Line;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final static String TEMPLATE = "Invoice_Template.pdf";
    private final static DateFormat DATE_FORMAT = new SimpleDateFormat( "dd/MM/yyyy" );

    @Autowired
    private ResourceLoader resourceLoader;

    @Override
    public List<String> getFields()
    {
        Resource resource = resourceLoader.getResource( "classpath:" + TEMPLATE );

        try ( PDDocument pdfDocument = PDDocument.load( resource.getInputStream() ) )
        {
            PDDocumentCatalog docCatalog = pdfDocument.getDocumentCatalog();
            PDAcroForm acroForm = docCatalog.getAcroForm();
            return acroForm.getFields().stream().map( PDField::getFullyQualifiedName ).collect( Collectors.toList() );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public ByteArrayOutputStream generate( Invoice invoice )
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Resource resource = resourceLoader.getResource( "classpath:" + TEMPLATE );

        try ( PDDocument pdfDocument = PDDocument.load( resource.getInputStream() ) )
        {
            PDDocumentCatalog docCatalog = pdfDocument.getDocumentCatalog();
            PDAcroForm acroForm = docCatalog.getAcroForm();
            acroForm.setCacheFields( true );

            setFields( invoice, acroForm );

            AccessPermission ap = new AccessPermission();
            ap.setCanModify( false );
            ap.setReadOnly();
            StandardProtectionPolicy spp = new StandardProtectionPolicy( UUID.randomUUID().toString(), "", ap );
            spp.setEncryptionKeyLength( 128 );
            pdfDocument.protect( spp );

            pdfDocument.save( out );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }

        return out;
    }

    private PDAcroForm setFields( Invoice invoice, PDAcroForm acroForm ) throws IOException
    {
        setField( acroForm, "Your Company Name", "ClouDesire.com" );
        setField( acroForm, "Your Name", invoice.getHolder().getEmail() );
        setField( acroForm, "Client's Name", invoice.getRecipient().getEmail() );
        setField( acroForm, "Invoice ID", invoice.getNumber() );
        setField( acroForm, "Issue Date", DATE_FORMAT.format( invoice.getDate() ) );

        ListIterator<Line> it = invoice.getLines().listIterator();
        while ( it.hasNext() )
        {
            int itemIndex = it.nextIndex() + 1;
            Line line = it.next();
            setField( acroForm, "Item " + itemIndex + ": Description", line.getDescription() );
            setField( acroForm, "Item " + itemIndex + ": Quantity", line.getQuantity().toPlainString() );
            setField( acroForm, "Item " + itemIndex + ": Unit Price", line.getPrice().getPrice().toPlainString() );
            setField( acroForm, "Item " + itemIndex + ": Amount", line.calculateTotalPrice().toPlainString() );
        }

        String total = invoice.getTotal().toPlainString();
        setField( acroForm, "Subtotal", total );
        setField( acroForm, "Amount Due", total );

        return acroForm;
    }

    private PDAcroForm setField( PDAcroForm acroForm, String name, String value ) throws IOException
    {
        PDField field = acroForm.getField( name );

        if ( field == null ) throw new RuntimeException( "No field found with name:" + name );

        field.setValue( value );
        field.setReadOnly( true );
        return acroForm;
    }
}
