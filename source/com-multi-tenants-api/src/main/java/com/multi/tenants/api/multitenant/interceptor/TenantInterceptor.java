package com.multi.tenants.api.multitenant.interceptor;

import com.multi.tenants.api.multitenant.context.TenantContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;

import javax.servlet.http.HttpServletRequest;

@Component
@Slf4j
public class TenantInterceptor implements WebRequestInterceptor {

    @Override
    public void preHandle(WebRequest request) throws Exception {
        String tenantId = null;
        if (request.getHeader("X-TENANT-ID") != null) {
            tenantId = request.getHeader("X-TENANT-ID");
        } else {
            tenantId = ((ServletWebRequest) request).getRequest().getServerName().split("\\.")[0];
        }

        TenantContext.setTenantId(tenantId);

        // Store tenantId as request attribute for TenantMigrationInterceptor
        if (request instanceof ServletWebRequest) {
            HttpServletRequest httpRequest = ((ServletWebRequest) request).getRequest();
            httpRequest.setAttribute("TENANT_ID", tenantId);
        }
    }

    @Override
    public void postHandle(WebRequest request, ModelMap model) throws Exception {
        TenantContext.clear();
    }

    @Override
    public void afterCompletion(WebRequest request, Exception ex) throws Exception {
    }
}