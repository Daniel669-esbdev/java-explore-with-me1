package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.NewEventDto;
import ru.practicum.service.EventService;
import ru.practicum.dto.*;
import ru.practicum.service.EventService;

import ru.practicum.dto.*;
import org.springframework.web.bind.annotation.PatchMapping;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class EventPrivateController {
    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getEventsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Private: запрос событй пользователем id={}, from={}, size={}", userId, from, size);
        return eventService.getEventsByUserId(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable Long userId,
                                 @Valid @RequestBody NewEventDto newEventDto) {
        log.info("Private: создание события пользователем id={}", userId);
        return eventService.createEvent(userId, newEventDto);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByUser(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @Valid @RequestBody UpdateEventUserRequest updateRequest) {
        log.info("Private: обновление события id={} пользователем id={}", eventId, userId);
        return eventService.updateEventByUser(userId, eventId, updateRequest);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getEventRequests(
            @PathVariable Long userId,
            @PathVariable Long eventId) {
        log.info("Private: запрос заявок на участие в событии id={} владельцем id={}", eventId, userId);
        return eventService.getEventRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestStatus(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody EventRequestStatusUpdateRequest updateRequest) {
        log.info("Private: изменение статуса заявок для события id={} пользователем id={}", eventId, userId);
        return eventService.updateRequestStatus(userId, eventId, updateRequest);
    }


}