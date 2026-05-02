package com.assignment3.project.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Value;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;

@Value
@Builder
public class ErrorResponse {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    OffsetDateTime timestamp;
    int status;
    String error;
    String message;
    String path;

    public static ErrorResponse of(HttpStatus status, String message, String path) {
        return ErrorResponse.builder()
                .timestamp(OffsetDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .build();
    }
}
