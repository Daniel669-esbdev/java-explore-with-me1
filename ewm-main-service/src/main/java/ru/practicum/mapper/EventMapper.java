package ru.practicum.mapper;

import org.mapstruct.*;
import ru.practicum.dto.*;
import ru.practicum.model.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {

        @Mapping(target = "category", source = "category", qualifiedByName = "categoryToDto")
        @Mapping(target = "initiator", source = "initiator", qualifiedByName = "userToShortDto")
        @Mapping(target = "location", source = "location", qualifiedByName = "locationToDto")
        EventFullDto toEventFullDto(Event event);

        @Mapping(target = "category", source = "category", qualifiedByName = "categoryToDto")
        @Mapping(target = "initiator", source = "initiator", qualifiedByName = "userToShortDto")
        EventShortDto toEventShortDto(Event event);

        @Mapping(target = "id", ignore = true)
        @Mapping(target = "initiator", ignore = true)
        @Mapping(target = "category", ignore = true)
        @Mapping(target = "state", ignore = true)
        @Mapping(target = "publishedOn", ignore = true)
        @Mapping(target = "createdOn", ignore = true)
        @Mapping(target = "views", ignore = true)
        @Mapping(target = "confirmedRequests", ignore = true)
        Event toEvent(NewEventDto newEventDto);

        List<EventShortDto> toEventShortDtoList(List<Event> events);
        List<EventFullDto> toEventFullDtoList(List<Event> events);

        @Named("categoryToDto")
        default CategoryDto categoryToDto(Category category) {
                if (category == null) return null;
                return CategoryDto.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .build();
        }

        @Named("userToShortDto")
        default UserShortDto userToShortDto(User user) {
                if (user == null) return null;
                return UserShortDto.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .build();
        }

        @Named("locationToDto")
        default LocationDto locationToDto(Location location) {
                if (location == null) return null;
                return LocationDto.builder()
                        .lat(location.getLat())
                        .lon(location.getLon())
                        .build();
        }
}