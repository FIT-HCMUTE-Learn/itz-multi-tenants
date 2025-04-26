package com.multi.tenants.api.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UploadFileDto {
    @ApiModelProperty(name = "filePath")
    private String filePath;

    @ApiModelProperty(name = "fileName")
    private String fileName;

    @ApiModelProperty(name = "ext")
    private String ext;
}
