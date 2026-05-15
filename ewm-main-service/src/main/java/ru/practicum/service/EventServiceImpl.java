package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.*;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.*;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;
    private final EventMapper eventMapper;

    @Override
    @Transactional
    public EventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequest request) {
        log.info("Admin update request for eventId={}, request={}", eventId, request);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.error("Event id={} not found for admin update", eventId);
                    return new NotFoundException("Event with id=" + eventId + " was not found");
                });

        if (request.getEventDate() != null) {
            if (request.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
                log.warn("Invalid event date for admin update: {}", request.getEventDate());
                throw new BadRequestException("Field: eventDate. Error: должно содержать дату, которая еще не наступила");
            }
            event.setEventDate(request.getEventDate());
        }

        if (request.getStateAction() != null) {
            if (request.getStateAction() == UpdateEventAdminRequest.StateAction.PUBLISH_EVENT) {
                if (event.getState() != EventState.PENDING) {
                    log.warn("Cannot publish event id={} in state {}", eventId, event.getState());
                    throw new ConflictException("Cannot publish the event because it's not in the right state: " + event.getState());
                }
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
                log.info("Event id={} published by admin", eventId);
            } else if (request.getStateAction() == UpdateEventAdminRequest.StateAction.REJECT_EVENT) {
                if (event.getState() == EventState.PUBLISHED) {
                    log.warn("Cannot reject already published event id={}", eventId);
                    throw new ConflictException("Cannot reject the event because it's already published");
                }
                event.setState(EventState.CANCELED);
                log.info("Event id={} rejected by admin", eventId);
            }
        }

        if (request.getCategory() != null) {
            Category category = categoryRepository.findById(request.getCategory())
                    .orElseThrow(() -> {
                        log.error("Category id={} not found for admin update", request.getCategory());
                        return new NotFoundException("Category with id=" + request.getCategory() + " was not found");
                    });
            event.setCategory(category);
        }

        if (request.getLocation() != null) {
            event.setLat(request.getLocation().getLat());
            event.setLon(request.getLocation().getLon());
        }

        if (request.getAnnotation() != null) event.setAnnotation(request.getAnnotation());
        if (request.getDescription() != null) event.setDescription(request.getDescription());
        if (request.getTitle() != null) event.setTitle(request.getTitle());
        if (request.getPaid() != null) event.setPaid(request.getPaid());

        if (request.getParticipantLimit() != null) {
            if (request.getParticipantLimit() < 0) {
                log.warn("Negative participant limit: {}", request.getParticipantLimit());
                throw new BadRequestException("Participant limit cannot be negative");
            }
            event.setParticipantLimit(request.getParticipantLimit());
        }

        if (request.getRequestModeration() != null) event.setRequestModeration(request.getRequestModeration());

        Event savedEvent = eventRepository.save(event);
        log.info("Event id={} successfully updated by admin", eventId);
        return eventMapper.toEventFullDto(savedEvent);
    }

    @Override
    public List<EventFullDto> getEventsAdmin(List<Long> users, List<String> states, List<Long> categories,
                                             LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
        log.info("Admin search for events: users={}, states={}, categories={}, start={}, end={}, from={}, size={}",
                users, states, categories, rangeStart, rangeEnd, from, size);
        List<EventState> eventStates = states == null ? null :
                states.stream().map(EventState::valueOf).collect(Collectors.toList());

        List<Event> events = eventRepository.findEventsAdmin(users, eventStates, categories, rangeStart, rangeEnd,
                PageRequest.of(from / size, size));

        log.info("Found {} events for admin", events.size());
        return events.stream()
                .map(eventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventShortDto> getEventsPublic(String text, List<Long> categories, Boolean paid,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                               Boolean onlyAvailable, String sort, int from, int size,
                                               HttpServletRequest request) {

        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }

        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new BadRequestException("RangeStart must be before RangeEnd");
        }

        List<Long> categoryIds = (categories != null && categories.isEmpty()) ? null : categories;

        Sort sortOrder = "VIEWS".equalsIgnoreCase(sort)
                ? Sort.by(Sort.Direction.DESC, "views")
                : Sort.by(Sort.Direction.DESC, "eventDate");

        Pageable pageable = PageRequest.of(from / size, size, sortOrder);

        List<Event> events = eventRepository.findEventsPublic(text, categoryIds, paid, rangeStart, rangeEnd, pageable);

        if (Boolean.TRUE.equals(onlyAvailable)) {
            events = events.stream()
                    .filter(e -> e.getParticipantLimit() == 0 ||
                            e.getConfirmedRequests() < e.getParticipantLimit())
                    .collect(Collectors.toList());
        }

        statsClient.saveHit("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());

        setViews(events);

        return events.stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventPublic(Long id, HttpServletRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event with id=" + id + " was not found"));

        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("Event must be published");
        }

        String uri = request.getRequestURI();
        String ip = request.getRemoteAddr();
        LocalDateTime now = LocalDateTime.now();

        try {
            statsClient.saveHit("ewm-main-service", uri, ip, now);
        } catch (Exception e) {
            log.error("Error saving hit: {}", e.getMessage());
        }

        EventFullDto dto = eventMapper.toEventFullDto(event);

        try {
            LocalDateTime start = event.getPublishedOn() != null ?
                    event.getPublishedOn().minusSeconds(1) :
                    event.getCreatedOn().minusHours(1);

            LocalDateTime end = now.plusSeconds(2);

            List<ViewStatsDto> stats = statsClient.getStats(start, end, List.of(uri), true);

            if (stats != null && !stats.isEmpty()) {
                dto.setViews(stats.get(0).getHits());
            } else {
                dto.setViews(0L);
            }
        } catch (Exception e) {
            log.error("Error getting stats: {}", e.getMessage());
            dto.setViews(0L);
        }

        return dto;
    }

    @Override
    @Transactional
    public EventFullDto addEventPrivate(Long userId, NewEventDto newEventDto) {
        log.info("User id={} adding new event: {}", userId, newEventDto);
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            log.warn("User id={} provided invalid event date: {}", userId, newEventDto.getEventDate());
            throw new BadRequestException("Field: eventDate. Error: должно содержать дату, которая еще не наступила");
        }

        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User id={} not found for adding event", userId);
                    return new NotFoundException("User with id=" + userId + " was not found");
                });

        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> {
                    log.error("Category id={} not found for adding event", newEventDto.getCategory());
                    return new NotFoundException("Category with id=" + newEventDto.getCategory() + " was not found");
                });

        Event event = eventMapper.toEvent(newEventDto);
        event.setInitiator(initiator);
        event.setCategory(category);

        if (newEventDto.getLocation() != null) {
            event.setLat(newEventDto.getLocation().getLat());
            event.setLon(newEventDto.getLocation().getLon());
        }

        event.setViews(0L);
        event.setConfirmedRequests(0);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventState.PENDING);

        Event savedEvent = eventRepository.save(event);
        log.info("User id={} successfully added event id={}", userId, savedEvent.getId());
        return eventMapper.toEventFullDto(savedEvent);
    }

    @Override
    public EventFullDto getEventPrivate(Long userId, Long eventId) {
        log.info("User id={} request for private event id={}", userId, eventId);
        Event event = eventRepository.findByIdAndInitiator_Id(eventId, userId)
                .orElseThrow(() -> {
                    log.error("Event id={} with initiator id={} not found", eventId, userId);
                    return new NotFoundException("Event with id=" + eventId + " was not found");
                });

        return eventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto updateEventPrivate(Long userId, Long eventId, UpdateEventUserRequest request) {
        log.info("User id={} updating event id={}, request={}", userId, eventId, request);
        Event event = eventRepository.findByIdAndInitiator_Id(eventId, userId)
                .orElseThrow(() -> {
                    log.error("Event id={} with initiator id={} not found for update", eventId, userId);
                    return new NotFoundException("Event with id=" + eventId + " was not found");
                });

        if (event.getState() == EventState.PUBLISHED) {
            log.warn("User id={} cannot update published event id={}", userId, eventId);
            throw new ConflictException("Only pending or canceled events can be changed");
        }

        if (request.getEventDate() != null) {
            if (request.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                log.warn("User id={} provided invalid update date: {}", userId, request.getEventDate());
                throw new BadRequestException("Event date must be at least 2 hours from now");
            }
            event.setEventDate(request.getEventDate());
        }

        if (request.getCategory() != null) {
            Category category = categoryRepository.findById(request.getCategory())
                    .orElseThrow(() -> {
                        log.error("Category id={} not found for user update", request.getCategory());
                        return new NotFoundException("Category with id=" + request.getCategory() + " was not found");
                    });
            event.setCategory(category);
        }

        if (request.getLocation() != null) {
            event.setLat(request.getLocation().getLat());
            event.setLon(request.getLocation().getLon());
        }

        if (request.getStateAction() != null) {
            if (request.getStateAction() == UpdateEventUserRequest.StateAction.SEND_TO_REVIEW) {
                event.setState(EventState.PENDING);
                log.info("Event id={} moved to state PENDING by user id={}", eventId, userId);
            } else if (request.getStateAction() == UpdateEventUserRequest.StateAction.CANCEL_REVIEW) {
                event.setState(EventState.CANCELED);
                log.info("Event id={} moved to state CANCELED by user id={}", eventId, userId);
            }
        }

        if (request.getAnnotation() != null) event.setAnnotation(request.getAnnotation());
        if (request.getDescription() != null) event.setDescription(request.getDescription());
        if (request.getTitle() != null) event.setTitle(request.getTitle());
        if (request.getPaid() != null) event.setPaid(request.getPaid());

        if (request.getParticipantLimit() != null) {
            if (request.getParticipantLimit() < 0) {
                log.warn("Negative participant limit in user update: {}", request.getParticipantLimit());
                throw new BadRequestException("Participant limit cannot be negative");
            }
            event.setParticipantLimit(request.getParticipantLimit());
        }

        if (request.getRequestModeration() != null) event.setRequestModeration(request.getRequestModeration());

        Event savedEvent = eventRepository.save(event);
        log.info("Event id={} successfully updated by user id={}", eventId, userId);
        return eventMapper.toEventFullDto(savedEvent);
    }

    @Override
    public List<EventShortDto> getEventsPrivate(Long userId, int from, int size) {
        log.info("User id={} requesting list of own events: from={}, size={}", userId, from, size);
        if (!userRepository.existsById(userId)) {
            log.error("User id={} not found for list request", userId);
            throw new NotFoundException("User with id=" + userId + " was not found");
        }

        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageable);

        setViews(events);

        log.info("User id={} has {} events", userId, events.size());
        return events.stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    private void setViews(List<Event> events) {
        if (events == null || events.isEmpty()) {
            return;
        }

        log.info("Setting views for {} events", events.size());

        LocalDateTime start = events.stream()
                .map(Event::getCreatedOn)
                .min(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now().minusYears(10))
                .minusSeconds(1);

        LocalDateTime end = LocalDateTime.now().plusSeconds(5);

        List<String> uris = events.stream()
                .map(event -> "/events/" + event.getId())
                .collect(Collectors.toList());

        try {
            List<ViewStatsDto> stats = statsClient.getStats(start, end, uris, true);

            if (stats != null && !stats.isEmpty()) {
                Map<String, Long> viewsMap = stats.stream()
                        .collect(Collectors.toMap(ViewStatsDto::getUri, ViewStatsDto::getHits));

                events.forEach(event -> {
                    String key = "/events/" + event.getId();
                    event.setViews(viewsMap.getOrDefault(key, 0L));
                });
            } else {
                events.forEach(event -> event.setViews(0L));
            }
        } catch (Exception e) {
            log.warn("Error retrieving stats for event list: {}", e.getMessage());
            events.forEach(event -> event.setViews(0L));
        }
    }

    @Override
    public List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId) {
        log.info("User id={} requesting participation requests for event id={}", userId, eventId);
        checkUserExists(userId);

        eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> {
                    log.error("Event id={} with initiator id={} not found for request retrieval", eventId, userId);
                    return new NotFoundException("Event with id=" + eventId + " was not found");
                });

        List<ParticipationRequest> requests = requestRepository.findAllByEventId(eventId);
        log.info("Found {} requests for event id={}", requests.size(), eventId);
        return requests.stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateEventRequestStatus(
            Long userId, Long eventId, EventRequestStatusUpdateRequest updateRequest) {
        log.info("User id={} updating request status for event id={}, request={}", userId, eventId, updateRequest);
        checkUserExists(userId);
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> {
                    log.error("Event id={} with initiator id={} not found for request status update", eventId, userId);
                    return new NotFoundException("Event with id=" + eventId + " was not found");
                });

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            log.info("Moderation disabled or no limit for event id={}, confirming all", eventId);
            return EventRequestStatusUpdateResult.builder()
                    .confirmedRequests(new ArrayList<>())
                    .rejectedRequests(new ArrayList<>())
                    .build();
        }

        if (event.getConfirmedRequests() >= event.getParticipantLimit()) {
            log.warn("Participant limit reached for event id={}", eventId);
            throw new ConflictException("The participant limit has been reached");
        }

        List<ParticipationRequest> requests = requestRepository.findAllById(updateRequest.getRequestIds());

        for (ParticipationRequest request : requests) {
            if (!request.getStatus().equals(RequestStatus.PENDING)) {
                log.warn("Request id={} is not in PENDING status", request.getId());
                throw new ConflictException("Request must have status PENDING");
            }
        }

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        if (updateRequest.getStatus().equals(RequestStatus.REJECTED)) {
            requests.forEach(r -> r.setStatus(RequestStatus.REJECTED));
            log.info("Rejected {} requests for event id={}", requests.size(), eventId);
            rejectedRequests = requestRepository.saveAll(requests).stream()
                    .map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList());
        } else {
            for (ParticipationRequest request : requests) {
                if (event.getConfirmedRequests() < event.getParticipantLimit()) {
                    request.setStatus(RequestStatus.CONFIRMED);
                    event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                    confirmedRequests.add(RequestMapper.toParticipationRequestDto(requestRepository.save(request)));
                } else {
                    request.setStatus(RequestStatus.REJECTED);
                    rejectedRequests.add(RequestMapper.toParticipationRequestDto(requestRepository.save(request)));
                }
            }
            log.info("Processed confirmation: {} confirmed, {} rejected for event id={}",
                    confirmedRequests.size(), rejectedRequests.size(), eventId);
        }

        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }

    private void checkUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            log.error("User id={} does not exist", userId);
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
    }
}