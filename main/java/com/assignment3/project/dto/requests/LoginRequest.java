package com.assignment3.project.dto.requests;

public record LoginRequest(
        String email,
        String password
) {
}
