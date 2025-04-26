package com.multi.tenants.api.controller;

import com.multi.tenants.api.dto.ApiMessageDto;
import com.multi.tenants.api.dto.ErrorCode;
import com.multi.tenants.api.dto.ResponseListDto;
import com.multi.tenants.api.dto.category.CategoryDto;
import com.multi.tenants.api.form.category.CreateCategoryForm;
import com.multi.tenants.api.form.category.UpdateCategoryForm;
import com.multi.tenants.api.form.category.UpdateCategoryOrdering;
import com.multi.tenants.api.form.category.UpdateCategoryOrderingForm;
import com.multi.tenants.api.mapper.CategoryMapper;
import com.multi.tenants.api.model.Category;
import com.multi.tenants.api.model.criteria.CategoryCriteria;
import com.multi.tenants.api.repository.CategoryRepository;
import com.multi.tenants.api.service.ITzBaseApiService;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/v1/category")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
@Validated
public class CategoryController extends ABasicController {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ITzBaseApiService mgrApiService;

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CATE_L')")
    public ApiMessageDto<ResponseListDto<List<CategoryDto>>> getCategoryList(
            @Valid @ModelAttribute CategoryCriteria categoryCriteria,
            Pageable pageable
    ) {
        Specification<Category> specification = categoryCriteria.getSpecification();
        Page<Category> page = categoryRepository.findAll(specification, pageable);

        ResponseListDto<List<CategoryDto>> responseListDto = new ResponseListDto<>(
                categoryMapper.fromEntityToDtoList(page.getContent()),
                page.getTotalElements(),
                page.getTotalPages()
        );
        ApiMessageDto<ResponseListDto<List<CategoryDto>>> apiMessageDto = new ApiMessageDto<>();
        apiMessageDto.setData(responseListDto);
        apiMessageDto.setMessage("Get category list successfully");

        return apiMessageDto;
    }

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CATE_V')")
    public ApiMessageDto<CategoryDto> getCategory(@PathVariable Long id) {
        ApiMessageDto<CategoryDto> apiMessageDto = new ApiMessageDto<>();

        Category category = categoryRepository.findById(id).orElse(null);
        if (category == null) {
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.CATEGORY_ERROR_NOT_FOUND);
            apiMessageDto.setMessage("Category not found");
            return apiMessageDto;
        }

        apiMessageDto.setData(categoryMapper.fromEntityToCategoryDto(category));
        apiMessageDto.setMessage("Get category successfully");
        return apiMessageDto;
    }

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CATE_C')")
    public ApiMessageDto<String> createCategory(
            @Valid @RequestBody CreateCategoryForm createCategoryForm,
            BindingResult bindingResult
    ) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();

        if (categoryRepository.existsByNameAndKind(createCategoryForm.getName(),createCategoryForm.getKind())) {
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.CATEGORY_ERROR_EXIST);
            apiMessageDto.setMessage("Category existed");
            return apiMessageDto;
        }

        Category category = categoryMapper.fromCreateCategory(createCategoryForm);
        categoryRepository.save(category);

        apiMessageDto.setMessage("Create category successfully");
        return apiMessageDto;
    }

    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CATE_U')")
    public ApiMessageDto<String> updateCategory(
            @Valid @RequestBody UpdateCategoryForm updateCategoryForm,
            BindingResult bindingResult
    ) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();

        Category category = categoryRepository.findById(updateCategoryForm.getCategoryId()).orElse(null);
        if (category == null) {
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.CATEGORY_ERROR_NOT_FOUND);
            apiMessageDto.setMessage("Category not found");
            return apiMessageDto;
        }

        if (!Objects.equals(category.getName(), updateCategoryForm.getName())) {
            if (categoryRepository.existsByNameAndKind(updateCategoryForm.getName(), category.getKind())) {
                apiMessageDto.setResult(false);
                apiMessageDto.setCode(ErrorCode.CATEGORY_ERROR_EXIST);
                apiMessageDto.setMessage("Category existed");
                return apiMessageDto;
            }
        }
        if (StringUtils.isNoneBlank(updateCategoryForm.getImage())) {
            if(!updateCategoryForm.getImage().equals(category.getImage())){
                mgrApiService.deleteFile(category.getImage());
            }
        }

        categoryMapper.mappingForUpdateServiceCategory(updateCategoryForm, category);
        categoryRepository.save(category);

        apiMessageDto.setMessage("Update category successfully");
        return apiMessageDto;
    }

    @PutMapping(value = "/update-ordering", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CATE_U_ORD')")
    @Transactional
    public ApiMessageDto<String> updateOrdering(
            @Valid @RequestBody UpdateCategoryOrderingForm updateCategoryOrderingForm,
            BindingResult bindingResult
    ) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();

        for (UpdateCategoryOrdering updateCategoryOrdering : updateCategoryOrderingForm.getUpdateCategoryOrderings()) {
            Long categoryId = updateCategoryOrdering.getCategoryId();
            Integer ordering = updateCategoryOrdering.getOrdering();

            Category category = categoryRepository.findById(categoryId).orElse(null);
            if (category == null) {
                apiMessageDto.setResult(false);
                apiMessageDto.setCode(ErrorCode.CATEGORY_ERROR_NOT_FOUND);
                apiMessageDto.setMessage("Category not found");
                return apiMessageDto;
            }

            category.setOrdering(ordering);
            categoryRepository.save(category);
        }

        apiMessageDto.setMessage("Ordering updated successfully");
        return apiMessageDto;
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CATE_D')")
    public ApiMessageDto<String> deleteCategory(@PathVariable Long id) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();

        Category category = categoryRepository.findById(id).orElse(null);
        if (category == null) {
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.CATEGORY_ERROR_NOT_FOUND);
            apiMessageDto.setMessage("Category not found");
            return apiMessageDto;
        }

        categoryRepository.deleteById(id);
        apiMessageDto.setMessage("Delete category successfully");

        return apiMessageDto;
    }
}
