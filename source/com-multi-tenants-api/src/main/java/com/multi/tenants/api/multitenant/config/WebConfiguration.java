package com.multi.tenants.api.multitenant.config;

import com.multi.tenants.api.multitenant.interceptor.TenantInterceptor;
import com.multi.tenants.api.multitenant.interceptor.TenantMigrationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    private final TenantInterceptor tenantInterceptor;
    private final TenantMigrationInterceptor tenantMigrationInterceptor;

    @Autowired
    public WebConfiguration(TenantInterceptor tenantInterceptor, TenantMigrationInterceptor tenantMigrationInterceptor) {
        this.tenantInterceptor = tenantInterceptor;
        this.tenantMigrationInterceptor = tenantMigrationInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // First set the tenant context
        registry.addWebRequestInterceptor(tenantInterceptor);

        // Then check and perform migration if needed
        registry.addInterceptor(tenantMigrationInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/static/**", "/webjars/**", "/error/**");
    }
}