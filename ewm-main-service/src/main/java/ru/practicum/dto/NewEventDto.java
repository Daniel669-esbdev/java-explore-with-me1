package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
public class NewEventDto {

    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;

    @NotNull
    private Long category;

    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @NotNull
    private LocationDto location;

    private Boolean paid = false;

    @PositiveOrZero
    private Integer participantLimit = 0;

    private Boolean requestModeration = true;

    @NotBlank
    @Size(min = 3, max = 120)
    private String title;

    public NewEventDto(String annotation, Long category, String description, LocalDateTime eventDate,
                       LocationDto location, Boolean paid, Integer participantLimit,
                       Boolean requestModeration, String title) {
        this.annotation = annotation;
        this.category = category;
        this.description = description;
        this.eventDate = eventDate;
        this.location = location != null ? new LocationDto(location.getLat(), location.getLon()) : null;
        this.paid = paid;
        this.participantLimit = participantLimit;
        this.requestModeration = requestModeration;
        this.title = title;
    }

    public static class NewEventDtoBuilder {

        public NewEventDtoBuilder location(LocationDto location) {
            this.location = location != null ? new LocationDto(location.getLat(), location.getLon()) : null;
            return this;
        }
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public Long getCategory() {
        return category;
    }

    public void setCategory(Long category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    public Integer getParticipantLimit() {
        return participantLimit;
    }

    public void setParticipantLimit(Integer participantLimit) {
        this.participantLimit = participantLimit;
    }

    public Boolean getRequestModeration() {
        return requestModeration;
    }

    public void setRequestModeration(Boolean requestModeration) {
        this.requestModeration = requestModeration;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocationDto getLocation() {
        return location != null ? new LocationDto(location.getLat(), location.getLon()) : null;
    }

    public void setLocation(LocationDto location) {
        this.location = location != null ? new LocationDto(location.getLat(), location.getLon()) : null;
    }
}