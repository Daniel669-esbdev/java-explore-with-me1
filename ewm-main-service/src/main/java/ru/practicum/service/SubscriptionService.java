package ru.practicum.service;

import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.SubscriptionDto;
import ru.practicum.dto.UserShortDto;

import java.util.List;

public interface SubscriptionService {
    SubscriptionDto followUser(Long userId, Long followingId);

    void unfollowUser(Long userId, Long followingId);

    List<UserShortDto> getMyFollowings(Long userId, int from, int size);

    List<UserShortDto> getMyFollowers(Long userId, int from, int size);

    List<EventShortDto> getFeed(Long userId, int from, int size);
}