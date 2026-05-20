package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.SubscriptionDto;
import ru.practicum.dto.UserShortDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.SubscriptionMapper;
import ru.practicum.model.Event;
import ru.practicum.model.EventState;
import ru.practicum.model.Subscription;
import ru.practicum.model.User;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.SubscriptionRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    @Override
    @Transactional
    public SubscriptionDto followUser(Long userId, Long followingId) {
        log.info("User id={} wants to follow user id={}", userId, followingId);
        if (userId.equals(followingId)) {
            throw new ConflictException("You cannot follow yourself.");
        }

        User follower = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new NotFoundException("User with id=" + followingId + " was not found"));

        if (subscriptionRepository.existsByFollowerIdAndFollowingId(userId, followingId)) {
            throw new ConflictException("You are already following this user.");
        }

        Subscription sub = SubscriptionMapper.toSubscription(follower, following);
        return SubscriptionMapper.toSubscriptionDto(subscriptionRepository.save(sub));
    }

    @Override
    @Transactional
    public void unfollowUser(Long userId, Long followingId) {
        log.info("User id={} wants to unfollow user id={}", userId, followingId);
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
        if (!userRepository.existsById(followingId)) {
            throw new NotFoundException("User with id=" + followingId + " was not found");
        }

        Subscription sub = subscriptionRepository.findByFollowerIdAndFollowingId(userId, followingId)
                .orElseThrow(() -> new NotFoundException("Subscription not found."));

        subscriptionRepository.delete(sub);
    }

    @Override
    public List<UserShortDto> getMyFollowings(Long userId, int from, int size) {
        log.info("Getting followings for user id={}, from={}, size={}", userId, from, size);
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
        Pageable pageable = PageRequest.of(from / size, size);
        return subscriptionRepository.findAllByFollowerId(userId, pageable).stream()
                .map(sub -> new UserShortDto(sub.getFollowing().getId(), sub.getFollowing().getName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserShortDto> getMyFollowers(Long userId, int from, int size) {
        log.info("Getting followers for user id={}, from={}, size={}", userId, from, size);
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
        Pageable pageable = PageRequest.of(from / size, size);
        return subscriptionRepository.findAllByFollowingId(userId, pageable).stream()
                .map(sub -> new UserShortDto(sub.getFollower().getId(), sub.getFollower().getName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<EventShortDto> getFeed(Long userId, int from, int size) {
        log.info("Getting feed for user id={}, from={}, size={}", userId, from, size);
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }

        Pageable pageable = PageRequest.of(from / size, size);
        List<Subscription> subs = subscriptionRepository.findAllByFollowerId(userId, Pageable.unpaged());
        List<Long> followingIds = subs.stream()
                .map(sub -> sub.getFollowing().getId())
                .collect(Collectors.toList());

        if (followingIds.isEmpty()) {
            return List.of();
        }

        List<Event> events = eventRepository.findAllByInitiatorIdInAndStateAndEventDateAfterOrderByEventDateDesc(
                followingIds, EventState.PUBLISHED, LocalDateTime.now(), pageable);

        return events.stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }
}