package com.assignment3.project.mappers;

import com.assignment3.project.dto.responses.ProjectResponse;
import com.assignment3.project.entities.Image;
import com.assignment3.project.entities.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {UserMapper.class, CategoryMapper.class})
public interface ProjectMapper {
    @Mappings({
            @Mapping(target = "imagePaths", expression = "java(mapImagePaths(entity))")
    })
    ProjectResponse toDto(Project entity);

    default List<String> mapImagePaths(Project entity) {
        if (entity == null || entity.getImages() == null) return List.of();
        return entity.getImages().stream()
                .map(Image::getRelativePath)
                .collect(Collectors.toList());
    }
}
