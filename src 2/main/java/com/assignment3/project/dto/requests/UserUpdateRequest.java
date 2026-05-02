package com.assignment3.project.dto.requests;

import com.assignment3.project.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateRequest {
    @Size(max = 255, message = "Full name must not exceed 255 characters")
    private String fullName;
    
    @Email(message = "Email should be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;
    
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    private String password;
    
    private UserRole role;
}

