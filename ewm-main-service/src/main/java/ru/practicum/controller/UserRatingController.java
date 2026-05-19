package ru.practicum.controller;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.RatingService;

@RestController
@RequestMapping("/users/{userId}/ratings")
@RequiredArgsConstructor
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class UserRatingController {

    private final RatingService ratingService;

    @PostMapping("/{eventId}/like")
    @ResponseStatus(HttpStatus.CREATED)
    public void addLike(@PathVariable Long userId, @PathVariable Long eventId) {
        ratingService.addLike(userId, eventId);
    }

    @PostMapping("/{eventId}/dislike")
    @ResponseStatus(HttpStatus.CREATED)
    public void addDislike(@PathVariable Long userId, @PathVariable Long eventId) {
        ratingService.addDislike(userId, eventId);
    }

    @DeleteMapping("/{eventId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeRating(@PathVariable Long userId, @PathVariable Long eventId) {
        ratingService.removeRating(userId, eventId);
    }
}