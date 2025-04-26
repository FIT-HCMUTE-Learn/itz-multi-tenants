package com.multi.tenants.api.validation;

import com.multi.tenants.api.validation.impl.TypeDoubleValidation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TypeDoubleValidation.class)
@Documented
public @interface TypeDouble {
    boolean allowNull() default false;

    String fieldName() default "Field";

    String message() default "Invalid double value";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    double min() default -Double.MAX_VALUE;

    double max() default Double.MAX_VALUE;
}