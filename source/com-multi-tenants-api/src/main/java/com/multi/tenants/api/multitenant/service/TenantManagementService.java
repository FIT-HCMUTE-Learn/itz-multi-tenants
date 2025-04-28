package com.multi.tenants.api.multitenant.service;

import com.multi.tenants.api.model.Tenant;

public interface TenantManagementService {
    Tenant createTenant(String tenantId, String db, String password);
}
