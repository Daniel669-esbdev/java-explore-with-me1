package ru.practicum.model;

import lombok.*;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "compilations")
@Getter
@Setter
@Builder
@NoArgsConstructor
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Boolean pinned;

    @Column(nullable = false, unique = true)
    private String title;

    @ElementCollection
    @CollectionTable(name = "compilation_events", joinColumns = @JoinColumn(name = "compilation_id"))
    @Column(name = "event_id")
    private Set<Long> events;

    public Compilation(Long id, Boolean pinned, String title, Set<Long> events) {
        this.id = id;
        this.pinned = pinned;
        this.title = title;
        this.events = events == null ? null : new HashSet<>(events);
    }

    public Set<Long> getEvents() {
        return events == null ? null : new HashSet<>(events);
    }

    public void setEvents(Set<Long> events) {
        this.events = events == null ? null : new HashSet<>(events);
    }

    public static class CompilationBuilder {
        private Set<Long> events;

        public CompilationBuilder events(Set<Long> events) {
            this.events = events == null ? null : new HashSet<>(events);
            return this;
        }
    }
}