package com.multi.tenants.api.validation;

import com.multi.tenants.api.validation.impl.PhoneNumberValidation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneNumberValidation.class)
@Documented
public @interface PhoneNumber {
    boolean allowNull() default false;

    String message() default "Phone must be 10 digits and start with 0";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
