package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.SubscriptionDto;
import ru.practicum.dto.UserShortDto;
import ru.practicum.service.SubscriptionService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/subscriptions")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserSubscriptionController {
    private final SubscriptionService subscriptionService;

    @PostMapping("/{followingId}")
    @ResponseStatus(HttpStatus.CREATED)
    public SubscriptionDto followUser(@PathVariable Long userId, @PathVariable Long followingId) {
        log.info("Private POST /users/{}/subscriptions/{}", userId, followingId);
        return subscriptionService.followUser(userId, followingId);
    }

    @DeleteMapping("/{followingId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unfollowUser(@PathVariable Long userId, @PathVariable Long followingId) {
        log.info("Private DELETE /users/{}/subscriptions/{}", userId, followingId);
        subscriptionService.unfollowUser(userId, followingId);
    }

    @GetMapping("/following")
    public List<UserShortDto> getMyFollowings(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Private GET /users/{}/subscriptions/following?from={}&size={}", userId, from, size);
        return subscriptionService.getMyFollowings(userId, from, size);
    }

    @GetMapping("/followers")
    public List<UserShortDto> getMyFollowers(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Private GET /users/{}/subscriptions/followers?from={}&size={}", userId, from, size);
        return subscriptionService.getMyFollowers(userId, from, size);
    }

    @GetMapping("/feed")
    public List<EventShortDto> getFeed(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Private GET /users/{}/subscriptions/feed?from={}&size={}", userId, from, size);
        return subscriptionService.getFeed(userId, from, size);
    }
}