package ru.practicum.exception;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
public class ApiError {
    private List<String> errors;
    private String message;
    private String reason;
    private String status;
    private String timestamp;

    public ApiError(List<String> errors, String message, String reason, String status, String timestamp) {
        this.errors = errors == null ? null : new ArrayList<>(errors);
        this.message = message;
        this.reason = reason;
        this.status = status;
        this.timestamp = timestamp;
    }

    public List<String> getErrors() {
        return errors == null ? null : new ArrayList<>(errors);
    }

    public void setErrors(List<String> errors) {
        this.errors = errors == null ? null : new ArrayList<>(errors);
    }

    public static class ApiErrorBuilder {
        private List<String> errors;

        public ApiErrorBuilder errors(List<String> errors) {
            this.errors = errors == null ? null : new ArrayList<>(errors);
            return this;
        }
    }
}