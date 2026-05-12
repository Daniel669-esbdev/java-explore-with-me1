package ru.practicum.controller;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.NewEventDto;
import ru.practicum.dto.UpdateEventUserRequest;
import ru.practicum.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Validated
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class UserEventController {
    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getEvents(
            @PathVariable Long userId,
            @PositiveOrZero @RequestParam(defaultValue = "0") int from,
            @Positive @RequestParam(defaultValue = "10") int size) {
        return eventService.getEventsPrivate(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(
            @PathVariable Long userId,
            @Valid @RequestBody NewEventDto newEventDto) {
        return eventService.addEventPrivate(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId) {
        return eventService.getEventPrivate(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @Valid @RequestBody UpdateEventUserRequest updateRequest) {
        return eventService.updateEventPrivate(userId, eventId, updateRequest);
    }
}