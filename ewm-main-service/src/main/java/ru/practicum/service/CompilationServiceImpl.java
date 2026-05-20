package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationRequest;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    @Override
    @Transactional
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        log.info("Adding new compilation: title={}, pinned={}", newCompilationDto.getTitle(), newCompilationDto.isPinned());
        Set<Long> eventIds = newCompilationDto.getEvents() != null ?
                newCompilationDto.getEvents() : new HashSet<>();

        Compilation compilation = Compilation.builder()
                .pinned(newCompilationDto.isPinned())
                .title(newCompilationDto.getTitle())
                .events(eventIds)
                .build();

        Compilation savedCompilation = compilationRepository.save(compilation);
        log.info("Compilation saved with id={}", savedCompilation.getId());
        return toCompilationDto(savedCompilation);
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        log.info("Deleting compilation with id={}", compId);
        if (!compilationRepository.existsById(compId)) {
            log.error("Compilation with id={} not found for deletion", compId);
            throw new NotFoundException("Compilation with id=" + compId + " was not found");
        }
        compilationRepository.deleteById(compId);
        log.info("Compilation with id={} deleted", compId);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateRequest) {
        log.info("Updating compilation with id={}", compId);
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> {
                    log.error("Compilation with id={} not found for update", compId);
                    return new NotFoundException("Compilation with id=" + compId + " was not found");
                });

        if (updateRequest.getPinned() != null) {
            log.debug("Updating pinned status to {}", updateRequest.getPinned());
            compilation.setPinned(updateRequest.getPinned());
        }
        if (updateRequest.getTitle() != null) {
            log.debug("Updating title to {}", updateRequest.getTitle());
            compilation.setTitle(updateRequest.getTitle());
        }
        if (updateRequest.getEvents() != null) {
            log.debug("Updating events list to ids: {}", updateRequest.getEvents());
            compilation.setEvents(updateRequest.getEvents());
        }

        Compilation updatedCompilation = compilationRepository.save(compilation);
        log.info("Compilation with id={} updated", compId);
        return toCompilationDto(updatedCompilation);
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        log.info("Fetching compilations: pinned={}, from={}, size={}", pinned, from, size);
        PageRequest page = PageRequest.of(from / size, size);
        List<Compilation> compilations = (pinned != null) ?
                compilationRepository.findAllByPinned(pinned, page) :
                compilationRepository.findAll(page).getContent();

        log.info("Found {} compilations", compilations.size());
        return compilations.stream()
                .map(this::toCompilationDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilation(Long compId) {
        log.info("Fetching compilation with id={}", compId);
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> {
                    log.error("Compilation with id={} not found", compId);
                    return new NotFoundException("Compilation with id=" + compId + " was not found");
                });
        return toCompilationDto(compilation);
    }

    private CompilationDto toCompilationDto(Compilation compilation) {
        List<Event> events = eventRepository.findAllById(compilation.getEvents());
        return CompilationDto.builder()
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .events(events.stream()
                        .map(eventMapper::toEventShortDto)
                        .collect(Collectors.toList()))
                .build();
    }
}