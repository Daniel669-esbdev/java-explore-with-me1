package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.*;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.*;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final StatsClient statsClient;
    private final EventMapper eventMapper;

    @Override
    @Transactional
    public EventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (request.getEventDate() != null) {
            validateEventDate(request.getEventDate());
            event.setEventDate(request.getEventDate());
        }

        if (request.getStateAction() != null) {
            if (request.getStateAction() == UpdateEventAdminRequest.StateAction.PUBLISH_EVENT) {
                if (event.getState() != EventState.PENDING) {
                    throw new ConflictException("Событие можно публиковать только в состоянии PENDING");
                }
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else if (request.getStateAction() == UpdateEventAdminRequest.StateAction.REJECT_EVENT) {
                if (event.getState() == EventState.PUBLISHED) {
                    throw new ConflictException("Нельзя отклонить опубликованное событие");
                }
                event.setState(EventState.CANCELED);
            }
        }

        if (request.getCategory() != null) {
            Category category = categoryRepository.findById(request.getCategory())
                    .orElseThrow(() -> new NotFoundException("Category not found"));
            event.setCategory(category);
        }

        if (request.getLocation() != null) {
            event.setLat(request.getLocation().getLat());
            event.setLon(request.getLocation().getLon());
        }

        updateEventFields(event, request.getAnnotation(), request.getDescription(), request.getTitle(),
                request.getPaid(), request.getParticipantLimit(), request.getRequestModeration());

        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto getEventPublic(Long id, HttpServletRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event with id=" + id + " not found"));

        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("Event must be published");
        }

        statsClient.saveHit("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());
        return eventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto addEventPrivate(Long userId, NewEventDto newEventDto) {
        validateEventDate(newEventDto.getEventDate());

        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException("Category not found"));

        Event event = eventMapper.toEvent(newEventDto);
        event.setInitiator(initiator);
        event.setCategory(category);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventState.PENDING);
        event.setConfirmedRequests(0);
        event.setViews(0L);

        if (event.getPaid() == null) event.setPaid(false);
        if (event.getParticipantLimit() == null) event.setParticipantLimit(0);
        if (event.getRequestModeration() == null) event.setRequestModeration(true);

        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto getEventPrivate(Long userId, Long eventId) {
        return eventRepository.findByIdAndInitiator_Id(eventId, userId)
                .map(eventMapper::toEventFullDto)
                .orElseThrow(() -> new NotFoundException("Event not found"));
    }

    @Override
    @Transactional
    public EventFullDto updateEventPrivate(Long userId, Long eventId, UpdateEventUserRequest request) {
        Event event = eventRepository.findByIdAndInitiator_Id(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        if (event.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }

        if (request.getEventDate() != null) {
            validateEventDate(request.getEventDate());
            event.setEventDate(request.getEventDate());
        }

        if (request.getCategory() != null) {
            Category category = categoryRepository.findById(request.getCategory())
                    .orElseThrow(() -> new NotFoundException("Category not found"));
            event.setCategory(category);
        }

        if (request.getLocation() != null) {
            event.setLat(request.getLocation().getLat());
            event.setLon(request.getLocation().getLon());
        }

        if (request.getStateAction() != null) {
            if (request.getStateAction() == UpdateEventUserRequest.StateAction.SEND_TO_REVIEW) {
                event.setState(EventState.PENDING);
            } else if (request.getStateAction() == UpdateEventUserRequest.StateAction.CANCEL_REVIEW) {
                event.setState(EventState.CANCELED);
            }
        }

        updateEventFields(event, request.getAnnotation(), request.getDescription(), request.getTitle(),
                request.getPaid(), request.getParticipantLimit(), request.getRequestModeration());

        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    private void validateEventDate(LocalDateTime eventDate) {
        if (eventDate != null && eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("Event date must be at least 2 hours from now");
        }
    }

    private void updateEventFields(Event event, String annotation, String description, String title,
                                   Boolean paid, Integer participantLimit, Boolean requestModeration) {
        if (annotation != null) event.setAnnotation(annotation);
        if (description != null) event.setDescription(description);
        if (title != null) event.setTitle(title);
        if (paid != null) event.setPaid(paid);
        if (participantLimit != null) {
            if (participantLimit < 0) throw new BadRequestException("Limit cannot be negative");
            event.setParticipantLimit(participantLimit);
        }
        if (requestModeration != null) event.setRequestModeration(requestModeration);
    }

    @Override
    public List<EventShortDto> getEventsPrivate(Long userId, int from, int size) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found");
        }
        return eventRepository.findAllByInitiatorId(userId, PageRequest.of(from / size, size))
                .stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventFullDto> getEventsAdmin(List<Long> users, List<String> states, List<Long> categories,
                                             LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
        List<EventState> eventStates = null;
        if (states != null) {
            eventStates = states.stream()
                    .map(EventState::valueOf)
                    .collect(Collectors.toList());
        }

        List<Event> events = eventRepository.findEventsAdmin(users, eventStates, categories, rangeStart, rangeEnd,
                PageRequest.of(from / size, size));

        return events.stream()
                .map(eventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventShortDto> getEventsPublic(String text, List<Long> categories, Boolean paid,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                               Boolean onlyAvailable, String sort, int from, int size,
                                               HttpServletRequest request) {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new BadRequestException("Start must be before end");
        }

        List<Event> events = eventRepository.findEventsPublic(text, categories, paid, rangeStart, rangeEnd,
                PageRequest.of(from / size, size));

        if (Boolean.TRUE.equals(onlyAvailable)) {
            events = events.stream()
                    .filter(e -> e.getParticipantLimit() == 0 || e.getConfirmedRequests() < e.getParticipantLimit())
                    .collect(Collectors.toList());
        }

        statsClient.saveHit("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());

        return events.stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }
}