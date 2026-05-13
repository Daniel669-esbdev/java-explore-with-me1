package ru.practicum.model;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2000)
    private String annotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Category category;

    @Column(name = "confirmed_requests")
    private Integer confirmedRequests = 0;

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Column(nullable = false, length = 7000)
    private String description;

    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", nullable = false)
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private User initiator;

    @Embedded
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Location location;

    @Column(nullable = false)
    private Boolean paid = false;

    @Column(name = "participant_limit")
    private Integer participantLimit = 0;

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @Column(name = "request_moderation")
    private Boolean requestModeration = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventState state = EventState.PENDING;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false)
    private Long views = 0L;

    public Event(Long id, String annotation, Category category, Integer confirmedRequests,
                 LocalDateTime createdOn, String description, LocalDateTime eventDate, User initiator,
                 Location location, Boolean paid, Integer participantLimit, LocalDateTime publishedOn,
                 Boolean requestModeration, EventState state, String title, Long views) {
        this.id = id;
        this.annotation = annotation;
        this.category = category;
        this.confirmedRequests = confirmedRequests;
        this.createdOn = createdOn;
        this.description = description;
        this.eventDate = eventDate;
        this.initiator = initiator;
        this.location = location;
        this.paid = paid;
        this.participantLimit = participantLimit;
        this.publishedOn = publishedOn;
        this.requestModeration = requestModeration;
        this.state = state;
        this.title = title;
        this.views = views;
    }

    public void setLat(Float lat) {
        if (this.location == null) this.location = new Location();
        this.location.setLat(lat);
    }

    public void setLon(Float lon) {
        if (this.location == null) this.location = new Location();
        this.location.setLon(lon);
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public User getInitiator() {
        return initiator;
    }

    public void setInitiator(User initiator) {
        this.initiator = initiator;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event)) return false;
        Event event = (Event) o;
        return id != null && id.equals(event.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", state=" + state +
                ", eventDate=" + eventDate +
                ", views=" + views +
                '}';
    }

    public static EventBuilder builder() {
        return new EventBuilder();
    }

    public static class EventBuilder {
        private Long id;
        private String annotation;
        private Category category;
        private Integer confirmedRequests;
        private LocalDateTime createdOn;
        private String description;
        private LocalDateTime eventDate;
        private User initiator;
        private Location location;
        private Boolean paid;
        private Integer participantLimit;
        private LocalDateTime publishedOn;
        private Boolean requestModeration;
        private EventState state;
        private String title;
        private Long views;

        public EventBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public EventBuilder annotation(String annotation) {
            this.annotation = annotation;
            return this;
        }

        public EventBuilder category(Category category) {
            this.category = category;
            return this;
        }

        public EventBuilder confirmedRequests(Integer confirmedRequests) {
            this.confirmedRequests = confirmedRequests;
            return this;
        }

        public EventBuilder createdOn(LocalDateTime createdOn) {
            this.createdOn = createdOn;
            return this;
        }

        public EventBuilder description(String description) {
            this.description = description;
            return this;
        }

        public EventBuilder eventDate(LocalDateTime eventDate) {
            this.eventDate = eventDate;
            return this;
        }

        public EventBuilder initiator(User initiator) {
            this.initiator = initiator;
            return this;
        }

        public EventBuilder location(Location location) {
            this.location = location;
            return this;
        }

        public EventBuilder paid(Boolean paid) {
            this.paid = paid;
            return this;
        }

        public EventBuilder participantLimit(Integer participantLimit) {
            this.participantLimit = participantLimit;
            return this;
        }

        public EventBuilder publishedOn(LocalDateTime publishedOn) {
            this.publishedOn = publishedOn;
            return this;
        }

        public EventBuilder requestModeration(Boolean requestModeration) {
            this.requestModeration = requestModeration;
            return this;
        }

        public EventBuilder state(EventState state) {
            this.state = state;
            return this;
        }

        public EventBuilder title(String title) {
            this.title = title;
            return this;
        }

        public EventBuilder views(Long views) {
            this.views = views;
            return this;
        }

        public Event build() {
            return new Event(id, annotation, category, confirmedRequests, createdOn, description,
                    eventDate, initiator, location, paid, participantLimit, publishedOn,
                    requestModeration, state, title, views);
        }
    }
}