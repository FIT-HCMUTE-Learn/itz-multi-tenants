package com.multi.tenants.api.dto.setting;

import com.multi.tenants.api.dto.ABasicAdminDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SettingDto extends ABasicAdminDto {
    @ApiModelProperty(name = "keyName")
    private String keyName;

    @ApiModelProperty(name = "groupName")
    private String groupName;

    @ApiModelProperty(name = "valueData")
    private String valueData;

    @ApiModelProperty(name = "dataType")
    private String dataType;

    @ApiModelProperty(name = "description")
    private String description;

    @ApiModelProperty(name = "isSystem")
    private Boolean isSystem;
}
