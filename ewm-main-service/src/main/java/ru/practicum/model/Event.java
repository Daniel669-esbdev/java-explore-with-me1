package ru.practicum.model;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
@Builder
@AllArgsConstructor
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

    public Category getCategory() {
        return category != null ? new Category(category.getId(), category.getName()) : null;
    }

    public User getInitiator() {
        return initiator != null ? new User(initiator.getId(), initiator.getEmail(), initiator.getName()) : null;
    }

    public Location getLocation() {
        return location != null ? new Location(location.getLat(), location.getLon()) : null;
    }


    public void setCategory(Category category) {
        this.category = category != null ? new Category(category.getId(), category.getName()) : null;
    }

    public void setInitiator(User initiator) {
        this.initiator = initiator != null ? new User(initiator.getId(), initiator.getEmail(), initiator.getName()) : null;
    }

    public void setLocation(Location location) {
        this.location = location != null ? new Location(location.getLat(), location.getLon()) : null;
    }
}