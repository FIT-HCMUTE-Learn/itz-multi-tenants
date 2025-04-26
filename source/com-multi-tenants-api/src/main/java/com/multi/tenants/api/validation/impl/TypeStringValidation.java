package com.multi.tenants.api.validation.impl;

import com.multi.tenants.api.validation.TypeString;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class TypeStringValidation implements ConstraintValidator<TypeString, String> {
    private boolean allowNull;
    private boolean allowEmpty;
    private int maxLength;
    private String fieldName;
    private String messageNull;
    private String messageEmpty;
    private String messageMaxLength;

    @Override
    public void initialize(TypeString constraintAnnotation) {
        allowNull = constraintAnnotation.allowNull();
        allowEmpty = constraintAnnotation.allowEmpty();
        maxLength = constraintAnnotation.maxlength();
        fieldName = constraintAnnotation.fieldName();
        messageNull = constraintAnnotation.messageNull();
        messageEmpty = constraintAnnotation.messageEmpty();
        messageMaxLength = constraintAnnotation.messageMaxLength();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            if (!allowNull) {
                setCustomMessage(context, messageNull);
                return false;
            }
            return true;
        }
        if (!allowEmpty && StringUtils.isBlank(value)) {
            setCustomMessage(context, messageEmpty);
            return false;
        }
        if (value.trim().length() > maxLength) {
            setCustomMessage(context, messageMaxLength.replace("{maxLength}", String.valueOf(maxLength)));
            return false;
        }

        return true;
    }

    private void setCustomMessage(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message.replace("{fieldName}", fieldName))
                .addConstraintViolation();
    }
}