package ru.practicum.controller;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.service.RequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
@Validated
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class ParticipationRequestController {
    private final RequestService requestService;

    @GetMapping
    public List<ParticipationRequestDto> getRequests(@PathVariable Long userId) {
        log.info("GET /users/{}/requests", userId);
        return requestService.getUserRequests(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addRequest(
            @PathVariable Long userId,
            @RequestParam Long eventId) {
        log.info("POST /users/{}/requests: eventId={}", userId, eventId);
        return requestService.addRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(
            @PathVariable Long userId,
            @PathVariable Long requestId) {
        log.info("PATCH /users/{}/requests/{}/cancel", userId, requestId);
        return requestService.cancelRequest(userId, requestId);
    }
}