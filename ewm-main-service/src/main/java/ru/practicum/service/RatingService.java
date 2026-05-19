package ru.practicum.service;

import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.UserDto;

import java.util.List;

public interface RatingService {

    void addLike(Long userId, Long eventId);

    void addDislike(Long userId, Long eventId);

    void removeRating(Long userId, Long eventId);

    List<EventShortDto> getTopEvents(Integer from, Integer size);

    List<UserDto> getTopInitiators(Integer from, Integer size);
}