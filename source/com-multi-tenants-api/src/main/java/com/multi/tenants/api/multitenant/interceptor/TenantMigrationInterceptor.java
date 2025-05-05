package com.multi.tenants.api.multitenant.interceptor;

import com.multi.tenants.api.exception.TenantMigrationException;
import com.multi.tenants.api.multitenant.service.TenantMigrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class TenantMigrationInterceptor implements HandlerInterceptor {

    @Autowired
    private TenantMigrationService tenantMigrationService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Skip migration for system endpoints and static resources
        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/static") ||
                requestURI.startsWith("/webjars") ||
                requestURI.startsWith("/actuator") ||
                requestURI.startsWith("/api/token") ||
                requestURI.startsWith("/error")) {
            return true;
        }

        // Get tenantId from header or subdomain (already set by TenantInterceptor)
        String tenantId = (String) request.getAttribute("TENANT_ID");

        if (tenantId != null && !"BOOTSTRAP".equals(tenantId)) {
            boolean isMigrated = tenantMigrationService.checkAndMigrateTenant(tenantId);
            if (!isMigrated) {
                log.error("Failed to migrate tenant schema for tenantId: {}", tenantId);
                throw new TenantMigrationException("Schema migration failed", tenantId);
            }
        }

        return true;
    }
}