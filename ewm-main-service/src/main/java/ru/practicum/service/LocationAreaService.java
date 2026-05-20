package ru.practicum.service;

import ru.practicum.dto.LocationAreaDto;
import ru.practicum.dto.NewLocationAreaDto;

import java.util.List;

public interface LocationAreaService {
    LocationAreaDto create(NewLocationAreaDto dto);

    LocationAreaDto update(Long id, NewLocationAreaDto dto);

    LocationAreaDto getById(Long id);

    List<LocationAreaDto> getAll(int from, int size);

    void delete(Long id);
}