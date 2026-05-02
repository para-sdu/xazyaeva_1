package com.assignment3.project.mappers;

import com.assignment3.project.dto.responses.CategoryResponse;
import com.assignment3.project.entities.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryResponse toDto(Category entity);
    Category toEntity(CategoryResponse dto);
}
