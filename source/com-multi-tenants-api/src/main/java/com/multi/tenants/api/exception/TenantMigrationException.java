package com.multi.tenants.api.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TenantMigrationException extends RuntimeException {
    private String code;
    private String tenantId;

    public TenantMigrationException(String message, String tenantId) {
        super(message);
        this.tenantId = tenantId;
    }

    public TenantMigrationException(String message, String tenantId, String code) {
        super(message);
        this.tenantId = tenantId;
        this.code = code;
    }
}