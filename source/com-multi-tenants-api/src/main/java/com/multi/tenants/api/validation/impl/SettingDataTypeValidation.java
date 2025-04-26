package com.multi.tenants.api.validation.impl;

import com.multi.tenants.api.constant.ITzBaseConstant;
import com.multi.tenants.api.validation.SettingDataType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Set;

public class SettingDataTypeValidation implements ConstraintValidator<SettingDataType, String> {
    private boolean allowNull;
    private static final Set<String> VALID_VALUES = Set.of(
            ITzBaseConstant.SETTING_DATA_TYPE_STRING,
            ITzBaseConstant.SETTING_DATA_TYPE_INTEGER,
            ITzBaseConstant.SETTING_DATA_TYPE_LONG,
            ITzBaseConstant.SETTING_DATA_TYPE_DOUBLE
    );

    @Override
    public void initialize(SettingDataType constraintAnnotation) {
        allowNull = constraintAnnotation.allowNull();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return allowNull;
        }
        return !value.trim().isEmpty() && VALID_VALUES.contains(value.trim().toLowerCase());
    }
}
