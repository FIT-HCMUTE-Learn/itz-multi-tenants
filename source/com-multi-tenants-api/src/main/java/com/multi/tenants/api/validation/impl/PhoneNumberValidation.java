package com.multi.tenants.api.validation.impl;

import com.multi.tenants.api.validation.PhoneNumber;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PhoneNumberValidation implements ConstraintValidator<PhoneNumber, String> {
    private boolean allowNull;
    private static final String PHONE_PATTERN = "^0\\d{9}$";

    @Override
    public void initialize(PhoneNumber constraintAnnotation) {
        allowNull = constraintAnnotation.allowNull();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return allowNull;
        }
        return !value.trim().isEmpty() && value.matches(PHONE_PATTERN);
    }
}
