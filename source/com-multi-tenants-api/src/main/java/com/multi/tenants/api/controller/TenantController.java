package com.multi.tenants.api.controller;

import com.multi.tenants.api.dto.ApiMessageDto;
import com.multi.tenants.api.dto.ErrorCode;
import com.multi.tenants.api.dto.tenant.TenantAdminDto;
import com.multi.tenants.api.exception.BadRequestException;
import com.multi.tenants.api.form.tenant.CreateTenantAdminForm;
import com.multi.tenants.api.mapper.TenantMapper;
import com.multi.tenants.api.model.Tenant;
import com.multi.tenants.api.multitenant.service.TenantManagementService;
import com.multi.tenants.api.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/tenant")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class TenantController extends ABasicController {
    @Autowired
    private TenantManagementService tenantManagementService;
    @Autowired
    private TenantRepository tenantRepository;
    @Autowired
    private TenantMapper tenantMapper;

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<TenantAdminDto> createTenant(@Valid @RequestBody CreateTenantAdminForm form) {
        if (tenantRepository.existsByTenantId(form.getTenantId())) {
            throw new BadRequestException(ErrorCode.TENANT_ERROR_EXISTED);
        }

        Tenant tenant = tenantManagementService.createTenant(form.getTenantId(), form.getDb(), form.getPassword());
        ApiMessageDto<TenantAdminDto> apiMessageDto = new ApiMessageDto<>();
        apiMessageDto.setData(tenantMapper.fromEntityToTenantAdminDto(tenant));
        apiMessageDto.setMessage("Create tenant successfully");
        return apiMessageDto;
    }
}
