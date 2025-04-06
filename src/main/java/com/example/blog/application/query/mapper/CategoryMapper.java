package com.example.blog.application.query.mapper;

import com.example.blog.application.query.PostDTO;
import com.example.blog.domain.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.SPRING)
public interface CategoryMapper {

    @Mapping(target = "id", source = "id.id")
    PostDTO.CategoryDTO toDto(Category category);

}
