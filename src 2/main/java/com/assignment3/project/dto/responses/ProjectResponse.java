package com.assignment3.project.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ProjectResponse {
    private Long id;
    private String title;
    private String description;
    private UserResponse author;
    private List<String> imagePaths;
    private CategoryResponse category;
    private long targetAmount;
    private long collectedAmount;
    @JsonProperty("isVerified")
    private boolean isVerified;
}
