package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.*;
import ru.practicum.dto.*;
import ru.practicum.exception.*;
import ru.practicum.repository.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Transactional(readOnly = true)
    public List<EventFullDto> getEventsAdmin(List<Long> users, List<String> states, List<Long> categories,
                                             LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return eventRepository.findAllAdmin(users, states, categories, rangeStart, rangeEnd, pageRequest)
                .stream()
                .map(this::toFullDto)
                .collect(Collectors.toList());
    }

    public EventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequest updateRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (updateRequest.getEventDate() != null) {
            if (updateRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
                throw new ConflictException("Event date must be at least 1 hour after publication");
            }
            event.setEventDate(updateRequest.getEventDate());
        }

        if (updateRequest.getStateAction() != null) {
            if (event.getState() != EventState.PENDING) {
                throw new ConflictException("Cannot publish/reject the event because it is in state: " + event.getState());
            }
            if ("PUBLISH_EVENT".equals(updateRequest.getStateAction())) {
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else if ("REJECT_EVENT".equals(updateRequest.getStateAction())) {
                event.setState(EventState.CANCELED);
            }
        }

        patchEventFields(event, updateRequest.getAnnotation(), updateRequest.getDescription(), updateRequest.getCategory(),
                updateRequest.getPaid(), updateRequest.getParticipantLimit(), updateRequest.getRequestModeration(),
                updateRequest.getTitle(), updateRequest.getLocation());

        return toFullDto(eventRepository.save(event));
    }

    @Transactional(readOnly = true)
    public List<EventShortDto> getEventsPublic(String text, List<Long> categories, Boolean paid,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                               Boolean onlyAvailable, String sort, int from, int size) {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new ValidationException("Range start must be before range end");
        }

        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findAllPublic(text, categories, paid, rangeStart, rangeEnd, pageRequest);

        if (Boolean.TRUE.equals(onlyAvailable)) {
            events = events.stream()
                    .filter(e -> e.getParticipantLimit() == 0 || e.getConfirmedRequests() < e.getParticipantLimit())
                    .collect(Collectors.toList());
        }

        if ("EVENT_DATE".equals(sort)) {
            events.sort(Comparator.comparing(Event::getEventDate));
        } else if ("VIEWS".equals(sort)) {
            events.sort(Comparator.comparing(Event::getId));
        }

        return events.stream().map(this::toShortDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EventFullDto getEventByIdPublic(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event with id=" + id + " was not found"));

        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("Event must be published");
        }

        return toFullDto(event);
    }

    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Event date must be at least 2 hours in the future");
        }

        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException("Category was not found"));

        Event event = Event.builder()
                .annotation(newEventDto.getAnnotation())
                .category(category)
                .confirmedRequests(0L)
                .createdOn(LocalDateTime.now())
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .initiator(initiator)
                .location(newEventDto.getLocation())
                .paid(newEventDto.getPaid() != null ? newEventDto.getPaid() : false)
                .participantLimit(newEventDto.getParticipantLimit() != null ? newEventDto.getParticipantLimit() : 0)
                .requestModeration(newEventDto.getRequestModeration() != null ? newEventDto.getRequestModeration() : true)
                .state(EventState.PENDING)
                .title(newEventDto.getTitle())
                .build();

        return toFullDto(eventRepository.save(event));
    }

    public EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("User is not the initiator");
        }
        if (event.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }
        if (updateRequest.getEventDate() != null && updateRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("Event date must be at least 2 hours in the future");
        }

        patchEventFields(event, updateRequest.getAnnotation(), updateRequest.getDescription(), updateRequest.getCategory(),
                updateRequest.getPaid(), updateRequest.getParticipantLimit(), updateRequest.getRequestModeration(),
                updateRequest.getTitle(), updateRequest.getLocation());

        if (updateRequest.getStateAction() != null) {
            if ("SEND_TO_REVIEW".equals(updateRequest.getStateAction())) {
                event.setState(EventState.PENDING);
            } else if ("CANCEL_REVIEW".equals(updateRequest.getStateAction())) {
                event.setState(EventState.CANCELED);
            }
        }

        return toFullDto(eventRepository.save(event));
    }

    private void patchEventFields(Event event, String annotation, String description, Long categoryId,
                                  Boolean paid, Integer participantLimit, Boolean moderation,
                                  String title, Location location) {
        if (annotation != null) event.setAnnotation(annotation);
        if (description != null) event.setDescription(description);
        if (paid != null) event.setPaid(paid);
        if (participantLimit != null) event.setParticipantLimit(participantLimit);
        if (moderation != null) event.setRequestModeration(moderation);
        if (title != null) event.setTitle(title);
        if (location != null) event.setLocation(location);
        if (categoryId != null) {
            event.setCategory(categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new NotFoundException("Category not found")));
        }
    }

    @Transactional(readOnly = true)
    public List<EventShortDto> getEventsByUserId(Long userId, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return eventRepository.findAllByInitiatorId(userId, pageRequest)
                .stream()
                .map(this::toShortDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EventFullDto getEventByIdByUser(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));
        if (!event.getInitiator().getId().equals(userId)) throw new NotFoundException("Access denied");
        return toFullDto(event);
    }

    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));
        if (!event.getInitiator().getId().equals(userId)) throw new ValidationException("User is not initiator");
        return requestRepository.findAllByEventId(eventId).stream()
                .map(this::toParticipationDto)
                .collect(Collectors.toList());
    }

    public EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest updateRequest) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found"));
        List<ParticipationRequest> requests = requestRepository.findAllById(updateRequest.getRequestIds());
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult(new ArrayList<>(), new ArrayList<>());

        for (ParticipationRequest req : requests) {
            if (req.getStatus() != RequestStatus.PENDING) throw new ConflictException("Request must be PENDING");
            if ("CONFIRMED".equals(updateRequest.getStatus().name())) {
                if (event.getParticipantLimit() != 0 && event.getConfirmedRequests() >= event.getParticipantLimit()) {
                    throw new ConflictException("Limit reached");
                }
                req.setStatus(RequestStatus.CONFIRMED);
                event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                result.getConfirmedRequests().add(toParticipationDto(requestRepository.save(req)));
            } else {
                req.setStatus(RequestStatus.REJECTED);
                result.getRejectedRequests().add(toParticipationDto(requestRepository.save(req)));
            }
        }
        eventRepository.save(event);
        return result;
    }

    public List<ParticipationRequestDto> getRequestsByUserId(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        return requestRepository.findAllByRequesterId(userId).stream()
                .map(this::toParticipationDto)
                .collect(Collectors.toList());
    }

    public ParticipationRequestDto createParticipationRequest(Long userId, Long eventId) {
        User requester = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found"));

        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) throw new ConflictException("Duplicate request");
        if (event.getInitiator().getId().equals(userId)) throw new ConflictException("Initiator cannot request");
        if (event.getState() != EventState.PUBLISHED) throw new ConflictException("Event not published");
        if (event.getParticipantLimit() != 0 && event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ConflictException("Limit reached");
        }

        ParticipationRequest request = ParticipationRequest.builder()
                .requester(requester).event(event).created(LocalDateTime.now()).status(RequestStatus.PENDING).build();

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        }
        return toParticipationDto(requestRepository.save(request));
    }

    public ParticipationRequestDto cancelParticipationRequest(Long userId, Long requestId) {
        ParticipationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found"));
        if (!request.getRequester().getId().equals(userId)) throw new NotFoundException("Access denied");
        request.setStatus(RequestStatus.CANCELED);
        return toParticipationDto(requestRepository.save(request));
    }

    private EventShortDto toShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId()).annotation(event.getAnnotation())
                .category(new CategoryDto(event.getCategory().getId(), event.getCategory().getName()))
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(event.getEventDate().format(FORMATTER))
                .initiator(new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName()))
                .paid(event.getPaid()).title(event.getTitle()).views(0L).build();
    }

    private EventFullDto toFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId()).annotation(event.getAnnotation())
                .category(new CategoryDto(event.getCategory().getId(), event.getCategory().getName()))
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn().format(FORMATTER))
                .description(event.getDescription())
                .eventDate(event.getEventDate().format(FORMATTER))
                .initiator(new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName()))
                .location(event.getLocation()).paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn() != null ? event.getPublishedOn().format(FORMATTER) : null)
                .requestModeration(event.getRequestModeration())
                .state(event.getState().name()).title(event.getTitle()).views(0L).build();
    }

    private ParticipationRequestDto toParticipationDto(ParticipationRequest request) {
        return ParticipationRequestDto.builder()
                .id(request.getId()).created(request.getCreated())
                .event(request.getEvent().getId()).requester(request.getRequester().getId())
                .status(request.getStatus().name()).build();
    }
}