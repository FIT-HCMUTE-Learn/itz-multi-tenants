package com.multi.tenants.api.dto.account;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class LoginDto {
    @ApiModelProperty(name = "username")
    private String username;

    @ApiModelProperty(name = "phoneNumber")
    private String phoneNumber;

    @ApiModelProperty(name = "token")
    private String token;

    @ApiModelProperty(name = "fullName")
    private String fullName;

    @ApiModelProperty(name = "id")
    private long id;

    @ApiModelProperty(name = "expired")
    private Date expired;

    @ApiModelProperty(name = "kind")
    private Integer kind;

    @ApiModelProperty(name = "firebaseAppId")
    private String firebaseAppId;

    @ApiModelProperty(name = "firebaseApiKey")
    private String firebaseApiKey;

    @ApiModelProperty(name = "requirePhoneValidation")
    private Boolean requirePhoneValidation = false;
}
