package com.multi.tenants.api.validation.impl;

import com.multi.tenants.api.validation.Password;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordValidation implements ConstraintValidator<Password, String> {
    private boolean allowNull;
    private int minLength;
    private static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$";

    @Override
    public void initialize(Password constraintAnnotation) {
        allowNull = constraintAnnotation.allowNull();
        minLength = constraintAnnotation.minLength();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return allowNull;
        }
        return value.length() >= minLength && value.matches(PASSWORD_PATTERN);
    }
}
