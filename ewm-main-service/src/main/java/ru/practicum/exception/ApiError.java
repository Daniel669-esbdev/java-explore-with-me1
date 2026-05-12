package ru.practicum.exception;

import lombok.Builder;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class ApiError {
    private List<String> errors;
    private String message;
    private String reason;
    private String status;
    private String timestamp;

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