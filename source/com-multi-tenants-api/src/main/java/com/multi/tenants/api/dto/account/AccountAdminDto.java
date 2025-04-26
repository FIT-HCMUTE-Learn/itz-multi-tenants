package com.multi.tenants.api.dto.account;

import com.multi.tenants.api.dto.group.GroupDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class AccountAdminDto {
    @ApiModelProperty(name = "id")
    private Long id;

    @ApiModelProperty(name = "kind")
    private int kind;

    @ApiModelProperty(name = "username")
    private String username;

    @ApiModelProperty(name = "phone")
    private String phone;

    @ApiModelProperty(name = "email")
    private String email;

    @ApiModelProperty(name = "fullName")
    private String fullName;

    @ApiModelProperty(name = "status")
    private Integer status;

    @ApiModelProperty(name = "groupId")
    private Long groupId;

    @ApiModelProperty(name = "lastLogin")
    private Date lastLogin;

    @ApiModelProperty(name = "avatar")
    private String avatar;

    @ApiModelProperty(name = "isSuperAdmin")
    private Boolean isSuperAdmin;

    @ApiModelProperty(name = "group")
    private GroupDto group;
}
