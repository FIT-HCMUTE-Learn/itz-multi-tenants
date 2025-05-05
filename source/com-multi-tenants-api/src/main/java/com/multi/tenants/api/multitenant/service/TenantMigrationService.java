package com.multi.tenants.api.multitenant.service;

import com.multi.tenants.api.model.MigrationStatus;
import com.multi.tenants.api.model.Tenant;
import com.multi.tenants.api.repository.MigrationStatusRepository;
import com.multi.tenants.api.repository.TenantRepository;
import com.multi.tenants.api.multitenant.context.TenantContext;
import liquibase.Liquibase;
import liquibase.changelog.ChangeSet;
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
import java.util.List;
import java.util.Optional;
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
                log.debug("Tenant {} already migrated successfully", tenantId);
                return true;
            }

            // Check for in-progress migration
            if (hasInProgressMigration(tenantId)) {
                log.warn("Tenant {} has in-progress migration", tenantId);
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
            // First check our migration status table
            Optional<MigrationStatus> latestStatus = migrationStatusRepository.findLatestSuccessfulMigration(tenantId);

            if (!latestStatus.isPresent()) {
                log.debug("No successful migration found for tenant: {}", tenantId);
                return false;
            }

            // Now verify the actual database state
            Tenant tenant = tenantRepository.findById(tenantId).orElse(null);
            if (tenant == null) {
                return false;
            }

            String decryptedPassword = encryptionService.decrypt(tenant.getPassword(), secret, salt);

            try (Connection connection = DriverManager.getConnection(tenant.getUrl(), tenant.getDb(), decryptedPassword)) {

                Database database = DatabaseFactory.getInstance()
                        .findCorrectDatabaseImplementation(new JdbcConnection(connection));

                Liquibase liquibase = new Liquibase(
                        "liquibase/db.changelog-tenant.xml",
                        new ClassLoaderResourceAccessor(),
                        database
                );

                // Check for unrun changesets
                List<ChangeSet> unrunChangeSets = liquibase.listUnrunChangeSets(null);

                if (!unrunChangeSets.isEmpty()) {
                    log.info("Tenant {} has {} unrun changesets", tenantId, unrunChangeSets.size());
                    for (ChangeSet cs : unrunChangeSets) {
                        log.debug("Unrun changeset: {} from file: {}", cs.getId(), cs.getFilePath());
                    }
                    return false;
                }

                log.debug("Tenant {} has no unrun changesets", tenantId);
                return true;

            } catch (Exception e) {
                log.error("Error checking migration status for tenant: {}", tenantId, e);
                return false;
            }
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
            // Get tenant details
            Tenant tenant = tenantRepository.findById(tenantId).orElseThrow(
                    () -> new RuntimeException("Tenant not found: " + tenantId)
            );

            // Create new migration status record
            migrationStatus = new MigrationStatus();
            migrationStatus.setTenant(tenant);
            migrationStatus.setMigrationStatus("IN_PROGRESS");
            migrationStatus.setStartTime(new Date());
            migrationStatus.setIsSuccess(false);
            migrationStatus = migrationStatusRepository.save(migrationStatus);

            // Perform migration
            String changelogVersion = migrateTenantSchema(tenant, migrationStatus);

            // Update status to COMPLETED
            migrationStatus.setMigrationStatus("COMPLETED");
            migrationStatus.setIsSuccess(true);
            migrationStatus.setEndTime(new Date());
            migrationStatus.setErrorMessage(null);
            migrationStatus.setChangelogVersion(changelogVersion);
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

                // Optionally attempt rollback
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

    private String migrateTenantSchema(Tenant tenant, MigrationStatus migrationStatus) throws Exception {
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

            // Get list of changesets that will be run
            List<ChangeSet> unrunChangeSets = liquibase.listUnrunChangeSets(null);

            if (!unrunChangeSets.isEmpty()) {
                log.info("Applying {} changesets to tenant: {}", unrunChangeSets.size(), tenant.getTenantId());

                liquibase.update(""); // Empty contexts

                // Log the applied changesets
                StringBuilder appliedChangeSets = new StringBuilder();
                for (ChangeSet changeSet : unrunChangeSets) {
                    String changeSetInfo = String.format("ID: %s, File: %s",
                            changeSet.getId(), changeSet.getFilePath());
                    log.info("Applied ChangeSet - {}", changeSetInfo);
                    appliedChangeSets.append(changeSetInfo).append("; ");
                }

                // Return information about the last applied changeset
                ChangeSet lastChangeSet = unrunChangeSets.get(unrunChangeSets.size() - 1);
                return String.format("Applied %d changesets. Last: %s:%s",
                        unrunChangeSets.size(),
                        lastChangeSet.getFilePath(),
                        lastChangeSet.getId());
            } else {
                log.info("No migrations needed for tenant: {}", tenant.getTenantId());
                return "Database up to date";
            }
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

            // Rollback the last change set
            liquibase.rollback(1, "");

            log.info("Rolled back migration for tenant: {}", tenant.getTenantId());
            migrationStatus.setRollbackVersion("Rolled back 1 changeset");
        }
    }
}