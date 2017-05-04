package com.liberologico.janine.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target( ElementType.TYPE )
@Retention( RetentionPolicy.RUNTIME )
@Constraint( validatedBy = IdentifiableValidator.class )
public @interface Identifiable
{
    String message() default "One of the companyName-taxCode or firstName-lastName pairs must be present";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
