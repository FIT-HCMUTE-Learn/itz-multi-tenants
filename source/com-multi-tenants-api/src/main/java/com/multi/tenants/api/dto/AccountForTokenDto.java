package com.multi.tenants.api.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class AccountForTokenDto {
    @ApiModelProperty(name = "id")
    private Long id;

    @ApiModelProperty(name = "kind")
    private int kind;

    @ApiModelProperty(name = "username")
    private String username;

    @ApiModelProperty(name = "email")
    private String email;

    @ApiModelProperty(name = "fullName")
    private String fullName;

    @ApiModelProperty(name = "isSuperAdmin")
    private Boolean isSuperAdmin;
}
