package com.liberologico.janine.validation;

import com.liberologico.janine.entities.Recipient;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IdentifiableValidator implements ConstraintValidator<Identifiable, Recipient>
{
    @Override
    public void initialize( Identifiable constraintAnnotation )
    {
    }

    @Override
    public boolean isValid( Recipient recipient, ConstraintValidatorContext context )
    {
        return ( recipient.getCompanyName() != null && recipient.getTaxCode() != null )
                || ( recipient.getFirstName() != null && recipient.getLastName() != null );
    }
}
