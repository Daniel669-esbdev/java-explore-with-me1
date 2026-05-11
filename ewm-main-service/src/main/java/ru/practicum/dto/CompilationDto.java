package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompilationDto {
    private List<EventShortDto> events;
    private Long id;
    private Boolean pinned;
    private String title;

    public List<EventShortDto> getEvents() {
        return events == null ? null : new ArrayList<>(events);
    }

    public void setEvents(List<EventShortDto> events) {
        this.events = events == null ? null : new ArrayList<>(events);
    }

    public static class CompilationDtoBuilder {
        private List<EventShortDto> events;

        public CompilationDtoBuilder events(List<EventShortDto> events) {
            this.events = events == null ? null : new ArrayList<>(events);
            return this;
        }
    }
}