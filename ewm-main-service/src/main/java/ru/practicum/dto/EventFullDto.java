package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder(builderClassName = "EventFullDtoBuilder")
@NoArgsConstructor
public class EventFullDto {
    private String annotation;
    private CategoryDto category;
    private Integer confirmedRequests;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private Long id;
    private UserShortDto initiator;
    private LocationDto location;
    private LocationAreaDto locationArea;
    private Boolean paid;
    private Integer participantLimit;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;
    private Boolean requestModeration;
    private String state;
    private String title;
    private Long views;

    public EventFullDto(String annotation, CategoryDto category, Integer confirmedRequests,
                        String description, LocalDateTime eventDate, Long id,
                        UserShortDto initiator, LocationDto location, LocationAreaDto locationArea,
                        Boolean paid, Integer participantLimit, LocalDateTime publishedOn,
                        LocalDateTime createdOn, Boolean requestModeration,
                        String state, String title, Long views) {
        this.annotation = annotation;
        this.category = category != null ? new CategoryDto(category.getId(), category.getName()) : null;
        this.confirmedRequests = confirmedRequests;
        this.description = description;
        this.eventDate = eventDate;
        this.id = id;
        this.initiator = initiator != null ? new UserShortDto(initiator.getId(), initiator.getName()) : null;
        this.location = location != null ? new LocationDto(location.getLat(), location.getLon()) : null;
        this.locationArea = locationArea;
        this.paid = paid;
        this.participantLimit = participantLimit;
        this.publishedOn = publishedOn;
        this.createdOn = createdOn;
        this.requestModeration = requestModeration;
        this.state = state;
        this.title = title;
        this.views = views;
    }

    public CategoryDto getCategory() {
        return category != null ? new CategoryDto(category.getId(), category.getName()) : null;
    }

    public void setCategory(CategoryDto category) {
        this.category = category != null ? new CategoryDto(category.getId(), category.getName()) : null;
    }

    public UserShortDto getInitiator() {
        return initiator != null ? new UserShortDto(initiator.getId(), initiator.getName()) : null;
    }

    public void setInitiator(UserShortDto initiator) {
        this.initiator = initiator != null ? new UserShortDto(initiator.getId(), initiator.getName()) : null;
    }

    public LocationDto getLocation() {
        return location != null ? new LocationDto(location.getLat(), location.getLon()) : null;
    }

    public void setLocation(LocationDto location) {
        this.location = location != null ? new LocationDto(location.getLat(), location.getLon()) : null;
    }

    public static class EventFullDtoBuilder {
        private String annotation;
        private CategoryDto category;
        private Integer confirmedRequests;
        private String description;
        private LocalDateTime eventDate;
        private Long id;
        private UserShortDto initiator;
        private LocationDto location;
        private LocationAreaDto locationArea;
        private Boolean paid;
        private Integer participantLimit;
        private LocalDateTime publishedOn;
        private LocalDateTime createdOn;
        private Boolean requestModeration;
        private String state;
        private String title;
        private Long views;

        public EventFullDtoBuilder category(CategoryDto category) {
            this.category = category != null ? new CategoryDto(category.getId(), category.getName()) : null;
            return this;
        }

        public EventFullDtoBuilder initiator(UserShortDto initiator) {
            this.initiator = initiator != null ? new UserShortDto(initiator.getId(), initiator.getName()) : null;
            return this;
        }

        public EventFullDtoBuilder location(LocationDto location) {
            this.location = location != null ? new LocationDto(location.getLat(), location.getLon()) : null;
            return this;
        }
    }
}