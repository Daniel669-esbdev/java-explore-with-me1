package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UpdateEventAdminRequest {
    private String annotation;
    private Long category;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private LocationDto location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private StateAction stateAction;
    private String title;

    public enum StateAction {
        PUBLISH_EVENT, REJECT_EVENT
    }

    public LocationDto getLocation() {
        return location != null ? new LocationDto(location.getLat(), location.getLon()) : null;
    }

    public void setLocation(LocationDto location) {
        this.location = location != null ? new LocationDto(location.getLat(), location.getLon()) : null;
    }
}