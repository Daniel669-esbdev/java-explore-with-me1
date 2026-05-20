package ru.practicum.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ErrorHandler {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(final NotFoundException e) {
        return ApiError.builder()
                .errors(Collections.emptyList())
                .status(HttpStatus.NOT_FOUND.name())
                .reason("The required object was not found.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(formatter))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflict(final ConflictException e) {
        return ApiError.builder()
                .errors(Collections.emptyList())
                .status(HttpStatus.CONFLICT.name())
                .reason("For the requested operation the conditions are not met.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(formatter))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDataIntegrityViolation(final DataIntegrityViolationException e) {
        return ApiError.builder()
                .errors(Collections.emptyList())
                .status(HttpStatus.CONFLICT.name())
                .reason("Integrity constraint has been violated.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(formatter))
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValid(final MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> String.format("Field: %s. Error: %s. Value: %s",
                        error.getField(), error.getDefaultMessage(), error.getRejectedValue()))
                .collect(Collectors.joining(", "));

        return ApiError.builder()
                .errors(Collections.emptyList())
                .status(HttpStatus.BAD_REQUEST.name())
                .reason("Incorrectly made request.")
                .message(message)
                .timestamp(LocalDateTime.now().format(formatter))
                .build();
    }

    @ExceptionHandler({
            BadRequestException.class,
            ConstraintViolationException.class,
            MissingServletRequestParameterException.class,
            IllegalArgumentException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequest(final Exception e) {
        return ApiError.builder()
                .errors(Collections.emptyList())
                .status(HttpStatus.BAD_REQUEST.name())
                .reason("Incorrectly made request.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(formatter))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleThrowable(final Throwable e) {
        return ApiError.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.name())
                .reason("Error occurred")
                .message(e.getMessage())
                .errors(Arrays.stream(e.getStackTrace())
                        .map(StackTraceElement::toString)
                        .collect(Collectors.toList()))
                .timestamp(LocalDateTime.now().format(formatter))
                .build();
    }
}