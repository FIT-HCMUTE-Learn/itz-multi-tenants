package com.multi.tenants.api.dto.user;

import com.multi.tenants.api.dto.account.AccountDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class UserDto {
    @ApiModelProperty(name = "id")
    private Long id;

    @ApiModelProperty(name = "birthday")
    private Date birthday;

    @ApiModelProperty(name = "account")
    private AccountDto account;
}
