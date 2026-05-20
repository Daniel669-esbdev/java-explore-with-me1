package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.LocationAreaDto;
import ru.practicum.dto.NewLocationAreaDto;
import ru.practicum.model.Location;
import ru.practicum.model.LocationArea;

@UtilityClass
public class LocationAreaMapper {

    public static LocationArea toLocationArea(NewLocationAreaDto dto) {
        return LocationArea.builder()
                .name(dto.getName())
                .location(Location.builder()
                        .lat(dto.getLat())
                        .lon(dto.getLon())
                        .build())
                .radius(dto.getRadius())
                .build();
    }

    public static LocationAreaDto toLocationAreaDto(LocationArea area) {
        return LocationAreaDto.builder()
                .id(area.getId())
                .name(area.getName())
                .lat(area.getLocation().getLat())
                .lon(area.getLocation().getLon())
                .radius(area.getRadius())
                .build();
    }
}