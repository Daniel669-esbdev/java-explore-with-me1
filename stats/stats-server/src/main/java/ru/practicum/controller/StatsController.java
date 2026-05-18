package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.service.StatsService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsController {
    private final StatsService statsService;
    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveHit(@Valid @RequestBody EndpointHitDto hitDto) {
        log.info("Received hit request: {}", hitDto);
        statsService.saveHit(hitDto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(
            @RequestParam @DateTimeFormat(pattern = DATE_PATTERN) LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = DATE_PATTERN) LocalDateTime end,
            @RequestParam(name = "uris", required = false) List<String> uris,
            @RequestParam(name = "unique", defaultValue = "false") boolean unique) {

        log.info("Received stats request: start={}, end={}, uris={}, unique={}", start, end, uris, unique);

        if (start.isAfter(end)) {
            log.warn("Invalid date range: start is after end");
            throw new IllegalArgumentException("Start time must be before end time");
        }

        return statsService.getStats(start, end, uris, unique);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(IllegalArgumentException.class)
    @org.springframework.web.bind.annotation.ResponseStatus(org.springframework.http.HttpStatus.BAD_REQUEST)
    public java.util.Map<String, String> handleIllegalArgumentException(final IllegalArgumentException e) {
        return java.util.Map.of(
                "error", "Bad Request",
                "message", e.getMessage()
        );
    }
}