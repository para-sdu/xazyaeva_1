package com.assignment3.project.dto.responses;

public record LoginResponse(
        String token,
        String email
) {
}
