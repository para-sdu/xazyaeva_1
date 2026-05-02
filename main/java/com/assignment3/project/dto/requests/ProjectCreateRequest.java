package com.assignment3.project.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class ProjectCreateRequest {
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;
    
    @NotBlank(message = "Description is required")
    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;
    
    private List<Long> imageIds;
    
    @NotNull(message = "Author ID is required")
    private Long authorId;
    
    private Long categoryId;
    
    private Long targetAmount;
    
    private Long collectedAmount;
}
