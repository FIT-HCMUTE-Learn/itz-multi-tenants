
package com.multi.tenants.api.validation.impl;

import com.multi.tenants.api.constant.ITzBaseConstant;
import com.multi.tenants.api.validation.AccountStatus;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class AccountStatusValidation implements ConstraintValidator<AccountStatus, Integer> {
    private boolean allowNull;
    private static final List<Integer> VALID_VALUES = List.of(
            ITzBaseConstant.STATUS_ACTIVE,
            ITzBaseConstant.STATUS_PENDING,
            ITzBaseConstant.STATUS_LOCK,
            ITzBaseConstant.STATUS_DELETE
    );

    @Override
    public void initialize(AccountStatus constraintAnnotation) {
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
