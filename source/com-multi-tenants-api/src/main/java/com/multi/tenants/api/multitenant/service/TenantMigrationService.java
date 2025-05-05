package com.multi.tenants.api.multitenant.service;

import com.multi.tenants.api.model.MigrationStatus;
import com.multi.tenants.api.model.Tenant;
import com.multi.tenants.api.repository.MigrationStatusRepository;
import com.multi.tenants.api.repository.TenantRepository;
import com.multi.tenants.api.multitenant.context.TenantContext;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Slf4j
public class TenantMigrationService {
    private final EncryptionService encryptionService;
    private final TenantRepository tenantRepository;
    private final MigrationStatusRepository migrationStatusRepository;
    private final ConcurrentHashMap<String, Lock> tenantLocks = new ConcurrentHashMap<>();

    @Value("${encryption.secret}")
    private String secret;

    @Value("${encryption.salt}")
    private String salt;

    @Autowired
    public TenantMigrationService(EncryptionService encryptionService,
                                  TenantRepository tenantRepository,
                                  MigrationStatusRepository migrationStatusRepository) {
        this.encryptionService = encryptionService;
        this.tenantRepository = tenantRepository;
        this.migrationStatusRepository = migrationStatusRepository;
    }

    public boolean checkAndMigrateTenant(String tenantId) {
        Lock tenantLock = tenantLocks.computeIfAbsent(tenantId, k -> new ReentrantLock());

        if (!tenantLock.tryLock()) {
            // Another request is already migrating this tenant
            log.info("Tenant {} is currently being migrated, waiting...", tenantId);
            tenantLock.lock();
            try {
                // Check if migration is complete
                return isValidMigration(tenantId);
            } finally {
                tenantLock.unlock();
            }
        }

        try {
            // Check if already migrated successfully
            if (isValidMigration(tenantId)) {
                return true;
            }

            // Check for in-progress migration
            if (hasInProgressMigration(tenantId)) {
                return false;
            }

            // Perform migration
            return performMigration(tenantId);
        } finally {
            tenantLock.unlock();
        }
    }

    private boolean isValidMigration(String tenantId) {
        String currentTenant = TenantContext.getTenantId();
        TenantContext.setTenantId("BOOTSTRAP");

        try {
            return migrationStatusRepository.findLatestSuccessfulMigration(tenantId).isPresent();
        } finally {
            TenantContext.setTenantId(currentTenant);
        }
    }

    private boolean hasInProgressMigration(String tenantId) {
        String currentTenant = TenantContext.getTenantId();
        TenantContext.setTenantId("BOOTSTRAP");

        try {
            return migrationStatusRepository.findInProgressMigration(tenantId).isPresent();
        } finally {
            TenantContext.setTenantId(currentTenant);
        }
    }

    @Transactional
    public boolean performMigration(String tenantId) {
        String currentTenant = TenantContext.getTenantId();
        TenantContext.setTenantId("BOOTSTRAP");

        MigrationStatus migrationStatus = null;
        try {
            // Create new migration status record
            migrationStatus = new MigrationStatus();
            migrationStatus.setTenantId(tenantId);
            migrationStatus.setMigrationStatus("IN_PROGRESS");
            migrationStatus.setStartTime(new Date());
            migrationStatus.setIsSuccess(false);
            migrationStatus = migrationStatusRepository.save(migrationStatus);

            // Get tenant details
            Tenant tenant = tenantRepository.findById(tenantId).orElseThrow(
                    () -> new RuntimeException("Tenant not found: " + tenantId)
            );

            // Perform migration
            migrateTenantSchema(tenant, migrationStatus);

            // Update status to COMPLETED
            migrationStatus.setMigrationStatus("COMPLETED");
            migrationStatus.setIsSuccess(true);
            migrationStatus.setEndTime(new Date());
            migrationStatus.setErrorMessage(null);
            migrationStatusRepository.save(migrationStatus);

            log.info("Successfully migrated tenant: {}", tenantId);
            return true;

        } catch (Exception e) {
            log.error("Failed to migrate tenant: {}", tenantId, e);

            if (migrationStatus != null) {
                // Update status to FAILED
                migrationStatus.setMigrationStatus("FAILED");
                migrationStatus.setIsSuccess(false);
                migrationStatus.setEndTime(new Date());
                migrationStatus.setErrorMessage(e.getMessage());
                migrationStatusRepository.save(migrationStatus);

                // Attempt rollback
                try {
                    Tenant tenant = tenantRepository.findById(tenantId).orElse(null);
                    if (tenant != null) {
                        rollbackMigration(tenant, migrationStatus);
                    }
                } catch (Exception rollbackException) {
                    log.error("Failed to rollback migration for tenant: {}", tenantId, rollbackException);
                }
            }

            return false;
        } finally {
            TenantContext.setTenantId(currentTenant);
        }
    }

    private void migrateTenantSchema(Tenant tenant, MigrationStatus migrationStatus) throws Exception {
        String decryptedPassword = encryptionService.decrypt(
                tenant.getPassword(),
                secret,
                salt
        );

        try (Connection connection = DriverManager.getConnection(
                tenant.getUrl(),
                tenant.getDb(),
                decryptedPassword)) {

            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));

            Liquibase liquibase = new Liquibase(
                    "liquibase/db.changelog-tenant.xml",
                    new ClassLoaderResourceAccessor(),
                    database
            );

            // Get current version before migration
            String currentVersion = liquibase.getDatabase().getDatabaseChangeLogTableName() + ".FILENAME";

            liquibase.update(""); // Empty contexts

            // Get version after migration
            String newVersion = liquibase.getDatabase().getDatabaseChangeLogTableName() + ".FILENAME";
            migrationStatus.setChangelogVersion(newVersion);
        }
    }

    private void rollbackMigration(Tenant tenant, MigrationStatus migrationStatus) throws Exception {
        String decryptedPassword = encryptionService.decrypt(
                tenant.getPassword(),
                secret,
                salt
        );

        try (Connection connection = DriverManager.getConnection(
                tenant.getUrl(),
                tenant.getDb(),
                decryptedPassword)) {

            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));

            Liquibase liquibase = new Liquibase(
                    "liquibase/db.changelog-tenant.xml",
                    new ClassLoaderResourceAccessor(),
                    database
            );

            // Get current version before rollback
            String currentVersion = liquibase.getDatabase().getDatabaseChangeLogTableName() + ".FILENAME";

            // Rollback the last change set
            liquibase.rollback(1, "");

            // Get version after rollback
            String rollbackVersion = liquibase.getDatabase().getDatabaseChangeLogTableName() + ".FILENAME";
            migrationStatus.setRollbackVersion(rollbackVersion);
        }
    }
}