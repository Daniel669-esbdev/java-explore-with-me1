package ru.practicum.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class CompilationDto {
    private Long id;
    private Boolean pinned;
    private String title;
    private Set<EventShortDto> events;

    public CompilationDto(Long id, Boolean pinned, String title, Set<EventShortDto> events) {
        this.id = id;
        this.pinned = pinned;
        this.title = title;
        this.events = (events == null) ? null : new HashSet<>(events);
    }

    public Set<EventShortDto> getEvents() {
        return (events == null) ? null : new HashSet<>(events);
    }

    public void setEvents(Set<EventShortDto> events) {
        this.events = (events == null) ? null : new HashSet<>(events);
    }
}