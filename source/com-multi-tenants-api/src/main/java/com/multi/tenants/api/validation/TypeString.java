package com.multi.tenants.api.validation;

import com.multi.tenants.api.validation.impl.TypeStringValidation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TypeStringValidation.class)
@Documented
public @interface TypeString {
    boolean allowNull() default false;

    boolean allowEmpty() default false;

    int maxlength() default 255;

    String fieldName() default "Field";

    String message() default "Invalid string value";

    String messageNull() default "{fieldName} must not be null";
    String messageEmpty() default "{fieldName} must not be empty";
    String messageMaxLength() default "{fieldName} exceeds max length of {maxLength} characters";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}