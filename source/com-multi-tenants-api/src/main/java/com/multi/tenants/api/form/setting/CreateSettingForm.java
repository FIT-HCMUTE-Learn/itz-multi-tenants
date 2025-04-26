package com.multi.tenants.api.form.setting;

import com.multi.tenants.api.validation.SettingDataType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@ApiModel
public class CreateSettingForm {
    @NotEmpty(message = "groupName cant not be null")
    @ApiModelProperty(name = "groupName", required = true)
    private String groupName;

    @NotBlank(message = "keyName cannot be null")
    @ApiModelProperty(name = "keyName", required = true)
    private String keyName;

    @ApiModelProperty(name = "description")
    private String description;

    @NotNull(message = "isSystem cant not be null")
    @ApiModelProperty(name = "isSystem", required = true)
    private Boolean isSystem;

    @NotNull(message = "status can not be null")
    @ApiModelProperty(required = true)
    private Integer status;

    @NotBlank(message = "valueDate can not be null or empty")
    @ApiModelProperty(name = "valueData", required = true)
    private String valueData;

    @SettingDataType
    @ApiModelProperty(name = "dataType", required = true)
    private String dataType;
}
