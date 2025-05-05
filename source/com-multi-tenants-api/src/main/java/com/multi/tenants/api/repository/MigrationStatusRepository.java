package com.multi.tenants.api.repository;

import com.multi.tenants.api.model.MigrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MigrationStatusRepository extends JpaRepository<MigrationStatus, Long> {

    @Query("SELECT m FROM MigrationStatus m WHERE m.tenant.tenantId = :tenantId ORDER BY m.createdDate DESC")
    List<MigrationStatus> findByTenantIdOrderByCreatedDateDesc(@Param("tenantId") String tenantId);

    @Query("SELECT m FROM MigrationStatus m WHERE m.tenant.tenantId = :tenantId AND m.migrationStatus = 'COMPLETED' AND m.isSuccess = true ORDER BY m.createdDate DESC")
    Optional<MigrationStatus> findLatestSuccessfulMigration(@Param("tenantId") String tenantId);

    @Query("SELECT m FROM MigrationStatus m WHERE m.tenant.tenantId = :tenantId AND m.migrationStatus = 'IN_PROGRESS'")
    Optional<MigrationStatus> findInProgressMigration(@Param("tenantId") String tenantId);
}