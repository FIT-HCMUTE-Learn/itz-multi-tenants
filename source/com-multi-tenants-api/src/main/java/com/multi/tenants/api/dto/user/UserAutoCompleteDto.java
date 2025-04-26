package com.multi.tenants.api.dto.user;

import com.multi.tenants.api.dto.account.AccountAutoCompleteDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserAutoCompleteDto {
    @ApiModelProperty(name = "id")
    private Long id;

    @ApiModelProperty(name = "accountAutoCompleteDto")
    private AccountAutoCompleteDto accountAutoCompleteDto;
}
