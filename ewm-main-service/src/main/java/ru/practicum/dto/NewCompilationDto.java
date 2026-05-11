package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCompilationDto {
    private Set<Long> events;
    private boolean pinned = false;
    @NotBlank
    @Size(min = 1, max = 50)
    private String title;

    public Set<Long> getEvents() {
        return events == null ? null : new HashSet<>(events);
    }

    public void setEvents(Set<Long> events) {
        this.events = events == null ? null : new HashSet<>(events);
    }
}