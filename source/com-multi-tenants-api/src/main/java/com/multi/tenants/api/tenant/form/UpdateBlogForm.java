package com.multi.tenants.api.tenant.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "Update Blog Form")
public class UpdateBlogForm {
    @ApiModelProperty(name = "id", required = true)
    @NotNull(message = "id cannot be null")
    private Long id;

    @ApiModelProperty(name = "title", required = true)
    @NotBlank(message = "title cannot be null or empty")
    private String title;

    @ApiModelProperty(name = "content", required = true)
    @NotBlank(message = "content cannot be null or empty")
    private String content;
}
