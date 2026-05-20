package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Event;
import ru.practicum.model.EventState;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.model.RequestStatus;
import ru.practicum.model.User;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        log.info("Fetching all participation requests for user id={}", userId);
        if (!userRepository.existsById(userId)) {
            log.error("User with id={} not found", userId);
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
        List<ParticipationRequest> requests = requestRepository.findAllByRequesterId(userId);
        log.info("Found {} requests for user id={}", requests.size(), userId);
        return requests.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
        log.info("User id={} adding participation request for event id={}", userId, eventId);
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User id={} not found", userId);
                    return new NotFoundException("User with id=" + userId + " was not found");
                });

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.error("Event id={} not found", eventId);
                    return new NotFoundException("Event with id=" + eventId + " was not found");
                });

        if (event.getInitiator().getId().equals(userId)) {
            log.warn("User id={} attempted to request participation in their own event id={}", userId, eventId);
            throw new ConflictException("Initiator cannot request participation in their own event");
        }

        if (event.getState() != EventState.PUBLISHED) {
            log.warn("User id={} attempted to request participation in unpublished event id={}", userId, eventId);
            throw new ConflictException("Cannot participate in an unpublished event");
        }

        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            log.warn("User id={} already has a request for event id={}", userId, eventId);
            throw new ConflictException("Could not add duplicate request");
        }

        if (event.getParticipantLimit() > 0 && event.getConfirmedRequests() >= event.getParticipantLimit()) {
            log.warn("Participant limit reached for event id={}", eventId);
            throw new ConflictException("The participant limit has been reached");
        }

        RequestStatus status = (!event.getRequestModeration() || event.getParticipantLimit() == 0)
                ? RequestStatus.CONFIRMED
                : RequestStatus.PENDING;

        ParticipationRequest request = ParticipationRequest.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(requester)
                .status(status)
                .build();

        ParticipationRequest savedRequest = requestRepository.save(request);
        log.info("Request saved with id={} and status={}", savedRequest.getId(), status);

        if (status == RequestStatus.CONFIRMED) {
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
            log.info("Confirmed requests count incremented for event id={}", eventId);
        }

        return toDto(savedRequest);
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        log.info("User id={} cancelling request id={}", userId, requestId);
        ParticipationRequest request = requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> {
                    log.error("Request id={} for user id={} not found", requestId, userId);
                    return new NotFoundException("Request with id=" + requestId + " was not found");
                });

        if (request.getStatus() == RequestStatus.CONFIRMED) {
            Event event = request.getEvent();
            event.setConfirmedRequests(event.getConfirmedRequests() - 1);
            eventRepository.save(event);
            log.info("Confirmed requests count decremented for event id={}", event.getId());
        }

        request.setStatus(RequestStatus.CANCELED);
        ParticipationRequest updatedRequest = requestRepository.save(request);
        log.info("Request id={} status changed to CANCELED", requestId);
        return toDto(updatedRequest);
    }

    private ParticipationRequestDto toDto(ParticipationRequest request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .created(request.getCreated())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus().name())
                .build();
    }
}