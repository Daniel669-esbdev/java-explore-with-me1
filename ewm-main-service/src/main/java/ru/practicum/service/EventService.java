package ru.practicum.service;

import ru.practicum.dto.*;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    List<EventFullDto> getEventsAdmin(List<Long> users, List<String> states, List<Long> categories,
                                      LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size);

    EventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequest updateRequest);

    List<EventShortDto> getEventsPublic(String text, List<Long> categories, Boolean paid,
                                        LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                        Boolean onlyAvailable, String sort, int from, int size,
                                        HttpServletRequest request);

    EventFullDto getEventPublic(Long id, HttpServletRequest request);

    List<EventShortDto> getEventsPrivate(Long userId, int from, int size);

    EventFullDto addEventPrivate(Long userId, NewEventDto newEventDto);

    EventFullDto getEventPrivate(Long userId, Long eventId);

    EventFullDto updateEventPrivate(Long userId, Long eventId, UpdateEventUserRequest updateRequest);
}