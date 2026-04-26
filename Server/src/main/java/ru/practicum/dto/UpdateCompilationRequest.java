package ru.practicum.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
@Getter
@Setter
@Builder
@NoArgsConstructor
public class UpdateCompilationRequest {
    private Set<Long> events;

    private Boolean pinned;

    @Size(min = 1, max = 50)
    private String title;

    public UpdateCompilationRequest(Set<Long> events, Boolean pinned, String title) {
        this.events = (events == null) ? null : new HashSet<>(events);
        this.pinned = pinned;
        this.title = title;
    }

    public Set<Long> getEvents() {
        return (events == null) ? null : new HashSet<>(events);
    }

    public void setEvents(Set<Long> events) {
        this.events = (events == null) ? null : new HashSet<>(events);
    }
}