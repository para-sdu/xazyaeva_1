package com.assignment3.project.dto.responses;

import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private String role;
    private String avatarPath;
    private String docPath;
    private boolean isVerified;
}
