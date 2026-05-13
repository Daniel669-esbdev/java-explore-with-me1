package ru.practicum.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.NewEventDto;
import ru.practicum.model.Event;

import java.util.List;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface EventMapper {

        @Mapping(target = "category", source = "category")
        @Mapping(target = "initiator", source = "initiator")
        @Mapping(target = "location.lat", source = "location.lat")
        @Mapping(target = "location.lon", source = "location.lon")
        EventFullDto toEventFullDto(Event event);

        @Mapping(target = "category", source = "category")
        @Mapping(target = "initiator", source = "initiator")
        EventShortDto toEventShortDto(Event event);

        @Mapping(target = "id", ignore = true)
        @Mapping(target = "category", ignore = true)
        @Mapping(target = "initiator", ignore = true)
        @Mapping(target = "state", ignore = true)
        @Mapping(target = "publishedOn", ignore = true)
        @Mapping(target = "views", ignore = true)
        @Mapping(target = "confirmedRequests", ignore = true)
        @Mapping(target = "createdOn", ignore = true)
        @Mapping(target = "location", ignore = true)
        Event toEvent(NewEventDto newEventDto);

        List<EventShortDto> toEventShortDtoList(List<Event> events);

        List<EventFullDto> toEventFullDtoList(List<Event> events);
}