package ru.practicum.dto;

import lombok.Data;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Data
public class UpdateCompilationRequest {
    private Set<Long> events;
    private Boolean pinned;

    @Size(min = 1, max = 50)
    private String title;

    public Set<Long> getEvents() {
        return events == null ? null : new HashSet<>(events);
    }

    public void setEvents(Set<Long> events) {
        this.events = events == null ? null : new HashSet<>(events);
    }
}