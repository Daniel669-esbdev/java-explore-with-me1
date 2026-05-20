package ru.practicum.service;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.UserDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.Event;
import ru.practicum.model.EventState;
import ru.practicum.model.Like;
import ru.practicum.model.User;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.LikeRepository;
import ru.practicum.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class RatingServiceImpl implements RatingService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    @Override
    @Transactional
    public void addLike(Long userId, Long eventId) {
        setRating(userId, eventId, true);
    }

    @Override
    @Transactional
    public void addDislike(Long userId, Long eventId) {
        setRating(userId, eventId, false);
    }

    @Override
    @Transactional
    public void removeRating(Long userId, Long eventId) {
        checkUserExists(userId);
        checkEventExists(eventId);
        likeRepository.deleteByEventIdAndUserId(eventId, userId);
        updateEventStats(eventId);
    }

    @Override
    public List<EventShortDto> getTopEvents(Integer from, Integer size) {
        PageRequest page = PageRequest.of(from / size, size);
        return eventRepository.findAllByStateOrderByViewsDesc(EventState.PUBLISHED, page).stream()
                .sorted((e1, e2) -> Long.compare(getEventRating(e2.getId()), getEventRating(e1.getId())))
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDto> getTopInitiators(Integer from, Integer size) {
        PageRequest page = PageRequest.of(from / size, size);
        return userRepository.findAll(page).stream()
                .sorted((u1, u2) -> Long.compare(getUserRating(u2.getId()), getUserRating(u1.getId())))
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    private void setRating(Long userId, Long eventId, Boolean isLike) {
        User user = checkUserExists(userId);
        Event event = checkEventExists(eventId);

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Initiator cannot rate their own event");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Cannot rate unpublished event");
        }

        Optional<Like> existingLike = likeRepository.findByEventIdAndUserId(eventId, userId);

        if (existingLike.isPresent()) {
            Like like = existingLike.get();
            if (like.getIsLike().equals(isLike)) {
                throw new ConflictException("Rating already set");
            }
            like.setIsLike(isLike);
            likeRepository.save(like);
        } else {
            Like newLike = Like.builder()
                    .user(user)
                    .event(event)
                    .isLike(isLike)
                    .build();
            likeRepository.save(newLike);
        }
        updateEventStats(eventId);
    }

    private void updateEventStats(Long eventId) {
        Event event = checkEventExists(eventId);
        long likes = likeRepository.countLikesByEventId(eventId);
        long dislikes = likeRepository.countDislikesByEventId(eventId);
        event.setViews(likes - dislikes);
        eventRepository.save(event);
    }

    private long getEventRating(Long eventId) {
        return likeRepository.countLikesByEventId(eventId) - likeRepository.countDislikesByEventId(eventId);
    }

    private long getUserRating(Long userId) {
        List<Event> events = eventRepository.findAllByInitiatorId(userId, Pageable.unpaged());
        return events.stream().mapToLong(e -> getEventRating(e.getId())).sum();
    }

    private User checkUserExists(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private Event checkEventExists(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));
    }
}