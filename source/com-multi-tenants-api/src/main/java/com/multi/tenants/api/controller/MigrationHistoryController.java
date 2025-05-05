package com.multi.tenants.api.controller;

import com.multi.tenants.api.dto.ApiResponse;
import com.multi.tenants.api.model.MigrationStatus;
import com.multi.tenants.api.repository.MigrationStatusRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/migration-history")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class MigrationHistoryController extends ABasicController {

    @Autowired
    private MigrationStatusRepository migrationStatusRepository;

    @GetMapping(value = "/{tenantId}", produces = MediaType.APPLICATION_JSON_VALUE)
    // @PreAuthorize("hasRole('DB_V_HISTORY')")
    public ApiResponse<List<MigrationStatus>> getTenantMigrationHistory(@PathVariable String tenantId) {
        ApiResponse<List<MigrationStatus>> response = new ApiResponse<>();

        List<MigrationStatus> migrationHistory = migrationStatusRepository.findByTenantIdOrderByCreatedDateDesc(tenantId);
        response.setData(migrationHistory);
        response.setMessage("Migration history retrieved successfully");

        return response;
    }

    @GetMapping(value = "/current/{tenantId}", produces = MediaType.APPLICATION_JSON_VALUE)
    // @PreAuthorize("hasRole('DB_V_HISTORY')")
    public ApiResponse<MigrationStatus> getCurrentMigrationStatus(@PathVariable String tenantId) {
        ApiResponse<MigrationStatus> response = new ApiResponse<>();

        migrationStatusRepository.findLatestSuccessfulMigration(tenantId)
                .ifPresentOrElse(
                        status -> {
                            response.setData(status);
                            response.setMessage("Current migration status retrieved successfully");
                        },
                        () -> {
                            response.setResult(false);
                            response.setMessage("No successful migration found for tenant: " + tenantId);
                        }
                );

        return response;
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    // @PreAuthorize("hasRole('DB_V_HISTORY')")
    public ApiResponse<List<MigrationStatus>> getAllMigrationHistory() {
        ApiResponse<List<MigrationStatus>> response = new ApiResponse<>();

        List<MigrationStatus> allMigrations = migrationStatusRepository.findAll();
        response.setData(allMigrations);
        response.setMessage("All migration history retrieved successfully");

        return response;
    }
}