package com.multi.tenants.api.validation.impl;

import com.multi.tenants.api.validation.TypeDouble;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

public class TypeDoubleValidation implements ConstraintValidator<TypeDouble, BigDecimal> {
    private boolean allowNull;
    private BigDecimal minValue;
    private BigDecimal maxValue;
    private String fieldName;

    @Override
    public void initialize(TypeDouble constraintAnnotation) {
        this.allowNull = constraintAnnotation.allowNull();
        this.fieldName = constraintAnnotation.fieldName();
        this.minValue = BigDecimal.valueOf(constraintAnnotation.min());
        this.maxValue = BigDecimal.valueOf(constraintAnnotation.max());
    }

    @Override
    public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
        if (value == null) {
            return allowNull;
        }

        if (value.compareTo(minValue) < 0) {
            return buildViolation(
                    context,
                    String.format("%s must be greater than %s", fieldName, minValue.toPlainString())
            );
        }
        if (value.compareTo(maxValue) > 0) {
            return buildViolation(
                    context,
                    String.format("%s must be less than %s", fieldName, maxValue.toPlainString())
            );
        }

        return true;
    }

    private boolean buildViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
        return false;
    }
}
