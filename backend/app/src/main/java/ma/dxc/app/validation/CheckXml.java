package ma.dxc.app.validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = {ElementType.PARAMETER, ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CheckMimeXml.class)
public @interface CheckXml {
    String message() default "only xml file is supported";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
