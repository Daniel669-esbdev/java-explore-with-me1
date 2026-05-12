package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventAdminRequest {
    @Size(min = 20, max = 2000)
    private String annotation;
    private Long category;
    @Size(min = 20, max = 7000)
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private LocationDto location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private StateAction stateAction;
    @Size(min = 3, max = 120)
    private String title;

    public enum StateAction { PUBLISH_EVENT, REJECT_EVENT }

    public String getAnnotation() { return annotation; }
    public void setAnnotation(String annotation) { this.annotation = annotation; }
    public Long getCategory() { return category; }
    public void setCategory(Long category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getEventDate() { return eventDate; }
    public void setEventDate(LocalDateTime eventDate) { this.eventDate = eventDate; }
    public Boolean getPaid() { return paid; }
    public void setPaid(Boolean paid) { this.paid = paid; }
    public Integer getParticipantLimit() { return participantLimit; }
    public void setParticipantLimit(Integer participantLimit) { this.participantLimit = participantLimit; }
    public Boolean getRequestModeration() { return requestModeration; }
    public void setRequestModeration(Boolean requestModeration) { this.requestModeration = requestModeration; }
    public StateAction getStateAction() { return stateAction; }
    public void setStateAction(StateAction stateAction) { this.stateAction = stateAction; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public LocationDto getLocation() {
        return location != null ? new LocationDto(location.getLat(), location.getLon()) : null;
    }

    public void setLocation(LocationDto location) {
        this.location = location != null ? new LocationDto(location.getLat(), location.getLon()) : null;
    }
}