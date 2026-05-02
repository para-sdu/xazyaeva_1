package com.assignment3.project.mappers;

import com.assignment3.project.dto.responses.VolunteerEventResponse;
import com.assignment3.project.entities.VolunteerEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface VolunteerEventMapper {
    @Mapping(target = "participants", expression = "java(mapParticipants(entity))")
    VolunteerEventResponse toDto(VolunteerEvent entity);

    default List<com.assignment3.project.dto.responses.UserResponse> mapParticipants(VolunteerEvent entity) {
        if (entity == null || entity.getParticipants() == null) return List.of();
        return entity.getParticipants().stream()
                .map(user -> {
                    com.assignment3.project.dto.responses.UserResponse response = new com.assignment3.project.dto.responses.UserResponse();
                    response.setId(user.getId());
                    response.setFullName(user.getFullName());
                    response.setEmail(user.getEmail());
                    response.setRole(user.getRole().name());
                    response.setAvatarPath(user.getAvatarPath());
                    response.setDocPath(user.getDocPath());
                    response.setVerified(user.isVerified());
                    return response;
                })
                .collect(Collectors.toList());
    }
}

