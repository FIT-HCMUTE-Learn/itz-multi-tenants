package com.multi.tenants.api.form.category;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "Update Category Ordering")
public class UpdateCategoryOrdering {
    @ApiModelProperty(name = "categoryId", example = "1", required = true)
    @NotNull(message = "categoryId can not be null")
    private Long categoryId;

    @ApiModelProperty(name = "ordering", example = "1", required = true)
    @NotNull(message = "ordering can not be null")
    private Integer ordering;
}
