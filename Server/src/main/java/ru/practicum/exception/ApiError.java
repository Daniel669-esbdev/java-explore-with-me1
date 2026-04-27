package ru.practicum.exception;

import lombok.Builder;
import lombok.Getter;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.List;

@Getter
@Builder
@SuppressFBWarnings(value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class ApiError {
    private final List<String> errors;
    private final String message;
    private final String reason;
    private final String status;
    private final String timestamp;
}