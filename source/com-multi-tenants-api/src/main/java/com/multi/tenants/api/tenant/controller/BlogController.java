package com.multi.tenants.api.tenant.controller;

import com.multi.tenants.api.controller.ABasicController;
import com.multi.tenants.api.dto.ApiMessageDto;
import com.multi.tenants.api.tenant.domain.Blog;
import com.multi.tenants.api.tenant.dto.BlogDto;
import com.multi.tenants.api.tenant.form.CreateBlogForm;
import com.multi.tenants.api.tenant.mapper.BlogMapper;
import com.multi.tenants.api.tenant.repository.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/blog")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class BlogController extends ABasicController {
    @Autowired
    private BlogRepository blogRepository;
    @Autowired
    private BlogMapper blogMapper;

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<BlogDto> createBlog(@Valid @RequestBody CreateBlogForm createBlogForm) {
        Blog blog = blogMapper.fromCreateBlogFormToEntity(createBlogForm);
        blogRepository.save(blog);

        ApiMessageDto<BlogDto> apiMessageDto = new ApiMessageDto<>();
        apiMessageDto.setData(blogMapper.fromEntityToBlogDto(blog));
        apiMessageDto.setMessage("Create blog successfully");
        return apiMessageDto;
    }
}
