package ru.practicum.model;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
@Builder
@NoArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2000)
    private String annotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "confirmed_requests", nullable = false)
    private Integer confirmedRequests = 0;

    @Column(nullable = false, length = 7000)
    private String description;

    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    @Column(name = "initiator_id", nullable = false)
    private Long initiatorId;

    @Embedded
    private Location location;

    @Column(nullable = false)
    private Boolean paid = false;

    @Column(name = "participant_limit", nullable = false)
    private Integer participantLimit = 0;

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @Column(name = "request_moderation", nullable = false)
    private Boolean requestModeration = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventState state = EventState.PENDING;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false)
    private Long views = 0L;

    public Event(Long id, String annotation, Category category, Integer confirmedRequests, String description,
                 LocalDateTime eventDate, Long initiatorId, Location location, Boolean paid, Integer participantLimit,
                 LocalDateTime publishedOn, Boolean requestModeration, EventState state, String title, Long views) {
        this.id = id;
        this.annotation = annotation;
        this.category = category == null ? null : new Category(category.getId(), category.getName());
        this.confirmedRequests = confirmedRequests;
        this.description = description;
        this.eventDate = eventDate;
        this.initiatorId = initiatorId;
        this.location = location == null ? null : new Location(location.getLat(), location.getLon());
        this.paid = paid;
        this.participantLimit = participantLimit;
        this.publishedOn = publishedOn;
        this.requestModeration = requestModeration;
        this.state = state;
        this.title = title;
        this.views = views;
    }

    public Category getCategory() {
        return category == null ? null : new Category(category.getId(), category.getName());
    }

    public void setCategory(Category category) {
        this.category = category == null ? null : new Category(category.getId(), category.getName());
    }

    public Location getLocation() {
        return location == null ? null : new Location(location.getLat(), location.getLon());
    }

    public void setLocation(Location location) {
        this.location = location == null ? null : new Location(location.getLat(), location.getLon());
    }

    public static class EventBuilder {
        private Category category;
        private Location location;

        public EventBuilder category(Category category) {
            this.category = category == null ? null : new Category(category.getId(), category.getName());
            return this;
        }

        public EventBuilder location(Location location) {
            this.location = location == null ? null : new Location(location.getLat(), location.getLon());
            return this;
        }
    }
}