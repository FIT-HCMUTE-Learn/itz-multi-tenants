package com.multi.tenants.api.tenant.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(description = "Create Blog Form")
public class CreateBlogForm {
    @ApiModelProperty(name = "title", required = true)
    @NotBlank(message = "title cannot be null or empty")
    private String title;

    @ApiModelProperty(name = "content", required = true)
    @NotBlank(message = "content cannot be null or empty")
    private String content;

    @ApiModelProperty(name = "author", required = true)
    @NotBlank(message = "author cannot be null or empty")
    private String author;
}
