package com.multi.tenants.api.dto.tenant;

import com.multi.tenants.api.dto.ABasicAdminDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(description = "Tenant Admin DTO")
public class TenantAdminDto extends ABasicAdminDto {
    @ApiModelProperty(name = "tenantId")
    private String tenantId;

    @ApiModelProperty(name = "db")
    private String db;

    @ApiModelProperty(name = "url")
    private String url;
}
