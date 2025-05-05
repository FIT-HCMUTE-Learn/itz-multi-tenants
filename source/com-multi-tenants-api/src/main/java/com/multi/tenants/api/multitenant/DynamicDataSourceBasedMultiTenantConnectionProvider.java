package com.multi.tenants.api.multitenant;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.multi.tenants.api.exception.TenantMigrationException;
import com.multi.tenants.api.multitenant.service.EncryptionService;
import com.multi.tenants.api.multitenant.service.TenantMigrationService;
import com.multi.tenants.api.model.Tenant;
import com.multi.tenants.api.repository.TenantRepository;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class DynamicDataSourceBasedMultiTenantConnectionProvider
        extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {

    private static final String TENANT_POOL_NAME_SUFFIX = "DataSource";

    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    @Qualifier("masterDataSource")
    private DataSource masterDataSource;

    @Autowired
    @Qualifier("masterDataSourceProperties")
    private DataSourceProperties dataSourceProperties;

    @Autowired
    private TenantRepository masterTenantRepository;

    @Autowired
    private TenantMigrationService tenantMigrationService;

    @Value("${multitenancy.datasource-cache.maximumSize:100}")
    private Long maximumSize;

    @Value("${multitenancy.datasource-cache.expireAfterAccess:10}")
    private Integer expireAfterAccess;

    @Value("${encryption.secret}")
    private String secret;

    @Value("${encryption.salt}")
    private String salt;

    private LoadingCache<String, DataSource> tenantDataSources;

    private final ConcurrentHashMap<String, Boolean> migrationCheckedTenants = new ConcurrentHashMap<>();

    @PostConstruct
    private void createCache() {
        tenantDataSources = CacheBuilder.newBuilder()
                .maximumSize(maximumSize)
                .expireAfterAccess(expireAfterAccess, TimeUnit.MINUTES)
                .removalListener((RemovalListener<String, DataSource>) removal -> {
                    HikariDataSource ds = (HikariDataSource) removal.getValue();
                    ds.close(); // tear down properly
                    log.info("Closed datasource: {}", ds.getPoolName());
                    // Remove migration check flag when datasource is evicted
                    migrationCheckedTenants.remove(removal.getKey());
                })
                .build(new CacheLoader<String, DataSource>() {
                    public DataSource load(String key) {
                        Tenant tenant = masterTenantRepository.findById(key)
                                .orElseThrow(() -> new RuntimeException("No such tenant: " + key));

                        // Create DataSource
                        DataSource dataSource = createAndConfigureDataSource(tenant);

                        // Check and migrate tenant schema
                        // This is a blocking call, so we need to handle it properly
                        try {
                            checkAndMigrateTenant(key);
                        } catch (Exception e) {
                            log.error("Migration failed for tenant: {}, but DataSource is still available", key, e);
                            // No throw exception here, we just log the error
                            // and continue to return the DataSource
                        }

                        return dataSource;
                    }
                });
    }

    @Override
    protected DataSource selectAnyDataSource() {
        return masterDataSource;
    }

    @Override
    protected DataSource selectDataSource(String tenantIdentifier) {
        if ("BOOTSTRAP".equals(tenantIdentifier)) {
            return masterDataSource;
        }
        try {
            return tenantDataSources.get(tenantIdentifier);
        } catch (ExecutionException e) {
            log.error("Failed to load DataSource for tenant: {}", tenantIdentifier, e);
            throw new RuntimeException("Failed to load DataSource for tenant: " + tenantIdentifier, e);
        }
    }

    private DataSource createAndConfigureDataSource(Tenant tenant) {
        String decryptedPassword = encryptionService.decrypt(tenant.getPassword(), secret, salt);

        HikariDataSource ds = dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();

        ds.setUsername(tenant.getDb());
        ds.setPassword(decryptedPassword);
        ds.setJdbcUrl(tenant.getUrl());

        ds.setPoolName(tenant.getTenantId() + TENANT_POOL_NAME_SUFFIX);

        log.info("Configured datasource: {}", ds.getPoolName());

        return ds;
    }

    private void checkAndMigrateTenant(String tenantId) {
        // Check if the tenant has already been checked for migration
        if (migrationCheckedTenants.containsKey(tenantId)) {
            return;
        }

        try {
            log.info("Checking migration for tenant: {}", tenantId);
            boolean isMigrated = tenantMigrationService.checkAndMigrateTenant(tenantId);
            if (!isMigrated) {
                log.error("Failed to migrate tenant schema for tenantId: {}", tenantId);
                throw new TenantMigrationException("Migration failed for tenant", tenantId);
            }
            // Mark tenant as migration checked
            migrationCheckedTenants.put(tenantId, true);
            log.info("Migration completed for tenant: {}", tenantId);
        } catch (TenantMigrationException e) {
            // Rethrow the exception to be handled by the caller
            throw e;
        } catch (Exception e) {
            log.error("Error checking migration for tenant: {}", tenantId, e);
            throw new TenantMigrationException("Migration check failed for tenant", tenantId);
        }
    }
}