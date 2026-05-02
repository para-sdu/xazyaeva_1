package com.assignment3.project.mappers;

import com.assignment3.project.dto.responses.UserResponse;
import com.assignment3.project.entities.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toDto(User entity);
    User toEntity(UserResponse dto);
}
