package ru.practicum.controller;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Validated
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class EventPublicController {

    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getEventsPublic(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) String sort,
            @PositiveOrZero @RequestParam(defaultValue = "0") int from,
            @Positive @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        log.info("GET /events: text={}, categories={}, paid={}, rangeStart={}, rangeEnd={}, onlyAvailable={}, sort={}, from={}, size={}, clientIp={}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request.getRemoteAddr());
        return eventService.getEventsPublic(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request);
    }

    @GetMapping("/{id}")
    public EventFullDto getEventPublic(@PathVariable Long id, HttpServletRequest request) {
        log.info("GET /events/{}: clientIp={}", id, request.getRemoteAddr());
        return eventService.getEventPublic(id, request);
    }
}