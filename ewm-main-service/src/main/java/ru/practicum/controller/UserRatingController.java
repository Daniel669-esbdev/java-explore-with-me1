package ru.practicum.controller;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.RatingService;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/ratings")
@RequiredArgsConstructor
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class UserRatingController {

    private final RatingService ratingService;

    @PostMapping("/{eventId}/like")
    @ResponseStatus(HttpStatus.CREATED)
    public void addLike(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("User id={} requested to ADD LIKE for event id={}", userId, eventId);
        ratingService.addLike(userId, eventId);
        log.info("User id={} successfully ADDED LIKE for event id={}", userId, eventId);
    }

    @PostMapping("/{eventId}/dislike")
    @ResponseStatus(HttpStatus.CREATED)
    public void addDislike(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("User id={} requested to ADD DISLIKE for event id={}", userId, eventId);
        ratingService.addDislike(userId, eventId);
        log.info("User id={} successfully ADDED DISLIKE for event id={}", userId, eventId);
    }

    @DeleteMapping("/{eventId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeRating(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("User id={} requested to REMOVE RATING for event id={}", userId, eventId);
        ratingService.removeRating(userId, eventId);
        log.info("User id={} successfully REMOVED RATING for event id={}", userId, eventId);
    }
}