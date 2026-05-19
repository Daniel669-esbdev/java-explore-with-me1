package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.UserDto;
import ru.practicum.service.RatingService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/ratings")
@RequiredArgsConstructor
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class PublicRatingController {

    private final RatingService ratingService;

    @GetMapping("/events/top")
    public List<EventShortDto> getTopEvents(
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        return ratingService.getTopEvents(from, size);
    }

    @GetMapping("/initiators/top")
    public List<UserDto> getTopInitiators(
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        return ratingService.getTopInitiators(from, size);
    }
}