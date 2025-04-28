package com.multi.tenants.api.mapper;

import com.multi.tenants.api.dto.tenant.TenantAdminDto;
import com.multi.tenants.api.model.Tenant;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {})
public interface TenantMapper {
    @Mapping(source = "tenantId", target = "tenantId")
    @Mapping(source = "db", target = "db")
    @Mapping(source = "url", target = "url")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToTenantAdminDto")
    TenantAdminDto fromEntityToTenantAdminDto(Tenant tenant);

    @IterableMapping(elementTargetType = TenantAdminDto.class, qualifiedByName = "fromEntityToTenantAdminDto")
    List<TenantAdminDto> fromEntitesToTenantAdminDtoList(List<Tenant> tenants);
}
