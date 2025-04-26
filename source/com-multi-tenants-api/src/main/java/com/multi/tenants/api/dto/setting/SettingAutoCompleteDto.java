package com.multi.tenants.api.dto.setting;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SettingAutoCompleteDto {
    @ApiModelProperty(name = "id")
    private Long id;

    @ApiModelProperty(name = "settingKey")
    private String settingKey;

    @ApiModelProperty(name = "groupName")
    private String groupName;
}
