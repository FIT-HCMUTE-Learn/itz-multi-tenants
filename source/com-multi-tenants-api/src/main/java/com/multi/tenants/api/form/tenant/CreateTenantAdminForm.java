package com.multi.tenants.api.form.tenant;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(description = "Create Tenant Admin Form")
public class CreateTenantAdminForm {
    @ApiModelProperty(name = "tenantId", required = true)
    @NotBlank(message = "tenantId can not be null or empty")
    private String tenantId;

    @ApiModelProperty(name = "db", required = true)
    @NotBlank(message = "db can not be null or empty")
    private String db;

    @ApiModelProperty(name = "password", required = true)
    @NotBlank(message = "password can not be null or empty")
    private String password;
}
