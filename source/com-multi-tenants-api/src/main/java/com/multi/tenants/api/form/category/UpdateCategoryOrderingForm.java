package com.multi.tenants.api.form.category;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@ApiModel(description = "Form for updating category ordering")
public class UpdateCategoryOrderingForm {
    @ApiModelProperty(name = "updateCategoryOrderings", required = true)
    @NotEmpty(message = "List of category orderings cannot be empty")
    @Valid
    private List<UpdateCategoryOrdering> updateCategoryOrderings;
}

