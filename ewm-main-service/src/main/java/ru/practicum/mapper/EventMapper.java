package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.NewEventDto;
import ru.practicum.model.Event;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper {

        @Mapping(target = "category", source = "category")
        @Mapping(target = "initiator", source = "initiator")
        @Mapping(target = "views", source = "views")
        @Mapping(target = "confirmedRequests", source = "confirmedRequests")
        @Mapping(target = "state", source = "state")
        @Mapping(target = "createdOn", source = "createdOn")
        @Mapping(target = "publishedOn", source = "publishedOn")
        EventFullDto toEventFullDto(Event event);

        @Mapping(target = "category", source = "category")
        @Mapping(target = "initiator", source = "initiator")
        @Mapping(target = "views", source = "views")
        @Mapping(target = "confirmedRequests", source = "confirmedRequests")
        EventShortDto toEventShortDto(Event event);

        @Mapping(target = "id", ignore = true)
        @Mapping(target = "initiator", ignore = true)
        @Mapping(target = "category", ignore = true)
        @Mapping(target = "state", ignore = true)
        @Mapping(target = "publishedOn", ignore = true)
        @Mapping(target = "views", ignore = true)
        @Mapping(target = "confirmedRequests", ignore = true)
        @Mapping(target = "createdOn", ignore = true)
        @Mapping(target = "lat", ignore = true)
        @Mapping(target = "lon", ignore = true)
        Event toEvent(NewEventDto newEventDto);

        List<EventShortDto> toEventShortDtoList(List<Event> events);

        List<EventFullDto> toEventFullDtoList(List<Event> events);
}