package com.multi.tenants.api.tenant.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "Blog DTO")
public class BlogDto {
    @ApiModelProperty(name = "id")
    private Long id;

    @ApiModelProperty(name = "title")
    private String title;

    @ApiModelProperty(name = "content")
    private String content;

    @ApiModelProperty(name = "author")
    private String author;
}
