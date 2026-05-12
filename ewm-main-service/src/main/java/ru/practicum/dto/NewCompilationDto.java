package ru.practicum.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
public class NewCompilationDto {
    private Set<Long> events;
    private boolean pinned = false;
    @NotBlank
    @Size(min = 1, max = 50)
    private String title;

    public NewCompilationDto(Set<Long> events, boolean pinned, String title) {
        this.events = events == null ? null : new HashSet<>(events);
        this.pinned = pinned;
        this.title = title;
    }

    public Set<Long> getEvents() {
        return events == null ? null : new HashSet<>(events);
    }

    public void setEvents(Set<Long> events) {
        this.events = events == null ? null : new HashSet<>(events);
    }
}