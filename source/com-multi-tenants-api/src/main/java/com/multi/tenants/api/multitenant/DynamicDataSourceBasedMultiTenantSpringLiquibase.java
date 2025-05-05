package com.multi.tenants.api.multitenant;

import com.multi.tenants.api.multitenant.service.EncryptionService;
import com.multi.tenants.api.model.Tenant;
import com.multi.tenants.api.repository.TenantRepository;
import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.xmlbeans.ResourceLoader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;

/**
 * Based on MultiTenantSpringLiquibase, this class provides Liquibase support for
 * multi-tenancy based on a dynamic collection of DataSources.
 */
@Component
@Getter
@Setter
@Slf4j
public class DynamicDataSourceBasedMultiTenantSpringLiquibase implements InitializingBean, ResourceLoaderAware {

    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    @Qualifier("tenantLiquibaseProperties")
    private LiquibaseProperties liquibaseProperties;

    @Value("${encryption.secret}")
    private String secret;

    @Value("${encryption.salt}")
    private String salt;

    private ResourceLoader resourceLoader;

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("Dynamic datasources based multitenancy enabled. Migration will be on-demand per tenant request.");
        // Do not run migration on startup anymore
        // this.runOnAllTenants(tenantRepository.findAll());
    }

    // Keep these methods for potential manual migration if needed
    protected void runOnAllTenants(Collection<Tenant> tenants) throws LiquibaseException {
        for (Tenant tenant : tenants) {
            log.info("Initializing Liquibase for tenant " + tenant.getTenantId());
            String decryptedPassword = encryptionService.decrypt(tenant.getPassword(), secret, salt);
            try (Connection connection = DriverManager.getConnection(tenant.getUrl(), tenant.getDb(), decryptedPassword)) {
                DataSource tenantDataSource = new SingleConnectionDataSource(connection, false);
                SpringLiquibase liquibase = this.getSpringLiquibase(tenantDataSource);
                liquibase.afterPropertiesSet();
            } catch (SQLException | LiquibaseException e) {
                log.error("Failed to run Liquibase for tenant " + tenant.getTenantId(), e);
            }
            log.info("Liquibase ran for tenant " + tenant.getTenantId());
        }
    }

    protected SpringLiquibase getSpringLiquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setResourceLoader((org.springframework.core.io.ResourceLoader) getResourceLoader());
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(liquibaseProperties.getChangeLog());
        liquibase.setContexts(liquibaseProperties.getContexts());
        return liquibase;
    }

    @Override
    public void setResourceLoader(org.springframework.core.io.ResourceLoader resourceLoader) {

    }
}