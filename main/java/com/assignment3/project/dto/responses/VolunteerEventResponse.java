package com.assignment3.project.dto.responses;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class VolunteerEventResponse {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime eventDate;
    private String location;
    private UserResponse organizer;
    private List<UserResponse> participants;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

