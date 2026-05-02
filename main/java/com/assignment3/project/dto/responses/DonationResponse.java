package com.assignment3.project.dto.responses;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DonationResponse {
    private Long id;
    private long amount;
    private UserResponse donor;
    private Long projectId;
    private LocalDateTime createdAt;
}

