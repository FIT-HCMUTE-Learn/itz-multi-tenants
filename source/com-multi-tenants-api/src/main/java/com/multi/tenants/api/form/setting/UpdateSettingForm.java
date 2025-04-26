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
public class UpdateSettingForm {
    @NotNull(message = "id cant not be null")
    @ApiModelProperty(name = "id", required = true)
    private Long id;

    @NotEmpty(message = "valueData cant not be null")
    @ApiModelProperty(name = "valueData", required = true)
    private String valueData;

    @ApiModelProperty(name = "description")
    private String description;

    @NotNull(message = "status cant not be null")
    @ApiModelProperty(name = "status", required = true)
    private Integer status;

    @NotBlank(message = "groupName cannot be null")
    @ApiModelProperty(name = "groupName", required = true)
    private String groupName;

    @NotBlank(message = "keyName cannot be null")
    @ApiModelProperty(name = "keyName", required = true)
    private String keyName;

    @SettingDataType
    @ApiModelProperty(name = "dataType", required = true)
    private String dataType;
}
