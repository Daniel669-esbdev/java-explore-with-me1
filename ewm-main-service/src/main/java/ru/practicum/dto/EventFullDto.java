package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
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
    private Boolean paid;
    private Integer participantLimit;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;
    private Boolean requestModeration;
    private String state;
    private String title;
    private Long views;

    public CategoryDto getCategory() {
        return category != null ? CategoryDto.builder().id(category.getId()).name(category.getName()).build() : null;
    }

    public void setCategory(CategoryDto category) {
        this.category = category != null ? CategoryDto.builder().id(category.getId()).name(category.getName()).build() : null;
    }

    public UserShortDto getInitiator() {
        return initiator != null ? UserShortDto.builder().id(initiator.getId()).name(initiator.getName()).build() : null;
    }

    public void setInitiator(UserShortDto initiator) {
        this.initiator = initiator != null ? UserShortDto.builder().id(initiator.getId()).name(initiator.getName()).build() : null;
    }

    public LocationDto getLocation() {
        return location != null ? LocationDto.builder().lat(location.getLat()).lon(location.getLon()).build() : null;
    }

    public void setLocation(LocationDto location) {
        this.location = location != null ? LocationDto.builder().lat(location.getLat()).lon(location.getLon()).build() : null;
    }

    public static class EventFullDtoBuilder {
        public EventFullDtoBuilder category(CategoryDto category) {
            this.category = category != null ? CategoryDto.builder().id(category.getId()).name(category.getName()).build() : null;
            return this;
        }

        public EventFullDtoBuilder initiator(UserShortDto initiator) {
            this.initiator = initiator != null ? UserShortDto.builder().id(initiator.getId()).name(initiator.getName()).build() : null;
            return this;
        }

        public EventFullDtoBuilder location(LocationDto location) {
            this.location = location != null ? LocationDto.builder().lat(location.getLat()).lon(location.getLon()).build() : null;
            return this;
        }
    }
}