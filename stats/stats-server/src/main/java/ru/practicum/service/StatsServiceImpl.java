package ru.practicum.service;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.model.EndpointHit;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class StatsServiceImpl implements StatsService {
    private final StatsRepository repository;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    @Transactional
    public void saveHit(EndpointHitDto hitDto) {
        EndpointHit hit = EndpointHit.builder()
                .app(hitDto.getApp())
                .uri(hitDto.getUri())
                .ip(hitDto.getIp())
                .timestamp(LocalDateTime.parse(hitDto.getTimestamp(), FORMATTER))
                .build();
        repository.saveAndFlush(hit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        log.info("Getting stats: start={}, end={}, uris={}, unique={}", start, end, uris, unique);

        if (start != null && end != null && start.isAfter(end)) {
            log.error("Validation failed: start date {} is after end date {}", start, end);
            throw new IllegalArgumentException("Start time must be before end time");
        }

        List<String> validUris = uris == null ? null : uris.stream()
                .filter(uri -> uri != null && !uri.isBlank())
                .collect(Collectors.toList());

        List<ViewStatsDto> results;

        if (validUris == null || validUris.isEmpty()) {
            results = unique ? repository.findUniqueStats(start, end) : repository.findAllStats(start, end);
        } else {
            results = unique ? repository.findUniqueStatsByUris(start, end, validUris) : repository.findStatsByUris(start, end, validUris);
        }

        if (results == null) {
            results = new ArrayList<>();
        }

        log.info("Found {} stats records", results.size());
        return results;
    }
}
