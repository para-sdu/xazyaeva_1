package com.assignment3.project.mappers;

import com.assignment3.project.dto.responses.DonationResponse;
import com.assignment3.project.entities.Donation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface DonationMapper {
    @Mapping(target = "projectId", source = "project.id")
    DonationResponse toDto(Donation entity);
}

