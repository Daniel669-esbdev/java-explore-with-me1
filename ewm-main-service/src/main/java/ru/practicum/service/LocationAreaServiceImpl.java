package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.LocationAreaDto;
import ru.practicum.dto.NewLocationAreaDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.LocationAreaMapper;
import ru.practicum.model.Location;
import ru.practicum.model.LocationArea;
import ru.practicum.repository.LocationAreaRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocationAreaServiceImpl implements LocationAreaService {

    private final LocationAreaRepository repository;

    @Override
    @Transactional
    public LocationAreaDto create(NewLocationAreaDto dto) {
        if (repository.findByName(dto.getName()).isPresent()) {
            throw new ConflictException("Location area with this name already exists");
        }
        LocationArea area = LocationAreaMapper.toLocationArea(dto);
        return LocationAreaMapper.toLocationAreaDto(repository.save(area));
    }

    @Override
    @Transactional
    public LocationAreaDto update(Long id, NewLocationAreaDto dto) {
        LocationArea area = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Location area not found with id: " + id));

        repository.findByName(dto.getName()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new ConflictException("Location area with this name already exists");
            }
        });

        area.setName(dto.getName());
        area.setLocation(Location.builder()
                .lat(dto.getLat())
                .lon(dto.getLon())
                .build());
        area.setRadius(dto.getRadius());

        return LocationAreaMapper.toLocationAreaDto(repository.save(area));
    }

    @Override
    public LocationAreaDto getById(Long id) {
        LocationArea area = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Location area not found with id: " + id));
        return LocationAreaMapper.toLocationAreaDto(area);
    }

    @Override
    public List<LocationAreaDto> getAll(int from, int size) {
        PageRequest page = PageRequest.of(from / size, size);
        return repository.findAll(page).stream()
                .map(LocationAreaMapper::toLocationAreaDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Location area not found with id: " + id);
        }
        repository.deleteById(id);
    }
}