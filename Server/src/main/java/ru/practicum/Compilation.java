package ru.practicum;

import lombok.*;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Entity
@Table(name = "compilations")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SuppressFBWarnings(value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Boolean pinned;

    @Column(nullable = false)
    private String title;

    @ManyToMany
    @JoinTable(
            name = "compilation_events",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    private Set<Event> events;

    public Set<Event> getEvents() {
        return events == null ? null : new HashSet<>(events);
    }

    public void setEvents(Set<Event> events) {
        this.events = events == null ? null : new HashSet<>(events);
    }
}