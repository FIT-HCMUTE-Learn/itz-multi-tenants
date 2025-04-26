package com.multi.tenants.api.validation.impl;

import com.multi.tenants.api.constant.ITzBaseConstant;
import com.multi.tenants.api.validation.CategoryKind;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class CategoryKindValidation implements ConstraintValidator<CategoryKind, Integer> {
    private boolean allowNull;
    private static final List<Integer> VALID_VALUES = List.of(
            ITzBaseConstant.CATEGORY_KIND_NEWS,
            ITzBaseConstant.CATEGORY_KIND_BRAND,
            ITzBaseConstant.CATEGORY_KIND_DEVICE,
            ITzBaseConstant.CATEGORY_KIND_DEVICE_COST_HISTORY
    );

    @Override
    public void initialize(CategoryKind constraintAnnotation) {
        allowNull = constraintAnnotation.allowNull();
    }

    @Override
    public boolean isValid(Integer kind, ConstraintValidatorContext constraintValidatorContext) {
        if (kind == null) {
            return allowNull;
        }
        return VALID_VALUES.contains(kind);
    }
}
