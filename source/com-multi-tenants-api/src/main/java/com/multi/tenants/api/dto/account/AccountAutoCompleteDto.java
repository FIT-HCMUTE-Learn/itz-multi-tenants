package com.multi.tenants.api.dto.account;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AccountAutoCompleteDto {
    @ApiModelProperty(name = "id")
    private long id;

    @ApiModelProperty(name = "fullName")
    private String fullName;

    @ApiModelProperty(name = "avatarPath")
    private String avatarPath;
}
