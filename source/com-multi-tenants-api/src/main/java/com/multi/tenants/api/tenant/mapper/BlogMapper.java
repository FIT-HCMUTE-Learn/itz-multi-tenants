package com.multi.tenants.api.tenant.mapper;

import com.multi.tenants.api.tenant.domain.Blog;
import com.multi.tenants.api.tenant.dto.BlogDto;
import com.multi.tenants.api.tenant.form.CreateBlogForm;
import com.multi.tenants.api.tenant.form.UpdateBlogForm;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {})
public interface BlogMapper {
    @Mapping(source = "title", target = "title")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "author", target = "author")
    Blog fromCreateBlogFormToEntity(CreateBlogForm createBlogForm);

    @Mapping(source = "title", target = "title")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "author", target = "author")
    void updateFromUpdateBlogForm(@MappingTarget Blog blog, UpdateBlogForm updateBlogForm);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "author", target = "author")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToBlogDto")
    BlogDto fromEntityToBlogDto(Blog blog);

    @IterableMapping(elementTargetType = BlogDto.class, qualifiedByName = "fromEntityToBlogDto")
    List<BlogDto> fromEntitiesToBlogDtoList(List<Blog> blogs);
}
