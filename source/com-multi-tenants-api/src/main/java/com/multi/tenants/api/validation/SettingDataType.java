package com.multi.tenants.api.validation;

import com.multi.tenants.api.validation.impl.SettingDataTypeValidation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SettingDataTypeValidation.class)
@Documented
public @interface SettingDataType {
    boolean allowNull() default false;

    String message() default "Setting data type invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
