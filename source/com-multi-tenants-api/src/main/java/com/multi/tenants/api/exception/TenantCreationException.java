package com.multi.tenants.api.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TenantCreationException extends RuntimeException {
    private String code;

    public TenantCreationException(String s) {
    }

    public TenantCreationException(String s, Exception e) {
    }
}
