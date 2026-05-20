package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.LocationAreaDto;
import ru.practicum.dto.NewLocationAreaDto;
import ru.practicum.service.LocationAreaService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/admin/locations")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AdminLocationAreaController {

    private final LocationAreaService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LocationAreaDto create(@Valid @RequestBody NewLocationAreaDto dto) {
        log.info("Admin request to create location area: {}", dto.getName());
        return service.create(dto);
    }

    @PatchMapping("/{id}")
    public LocationAreaDto update(@PathVariable Long id, @Valid @RequestBody NewLocationAreaDto dto) {
        log.info("Admin request to update location area id: {}", id);
        return service.update(id, dto);
    }

    @GetMapping("/{id}")
    public LocationAreaDto getById(@PathVariable Long id) {
        log.info("Admin request to get location area id: {}", id);
        return service.getById(id);
    }

    @GetMapping
    public List<LocationAreaDto> getAll(
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Admin request to get all location areas, from: {}, size: {}", from, size);
        return service.getAll(from, size);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        log.info("Admin request to delete location area id: {}", id);
        service.delete(id);
    }
}