package ru.practicum;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "compilations")
@Getter
@Setter
@NoArgsConstructor
@Builder
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

    public Compilation(Long id, Boolean pinned, String title, Set<Event> events) {
        this.id = id;
        this.pinned = pinned;
        this.title = title;
        this.events = (events == null) ? null : new java.util.HashSet<>(events);
    }

    public Set<Event> getEvents() {
        return (events == null) ? null : new java.util.HashSet<>(events);
    }

    public void setEvents(Set<Event> events) {
        this.events = (events == null) ? null : new java.util.HashSet<>(events);
    }
}
