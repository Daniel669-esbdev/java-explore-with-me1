package ru.practicum.exception;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiError {
    private String status;
    private String reason;
    private String message;
    private String timestamp;
}