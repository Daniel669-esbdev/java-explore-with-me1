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

    public static class EventFullDtoBuilder {
        private CategoryDto category;
        private UserShortDto initiator;
        private LocationDto location;

        public EventFullDtoBuilder category(CategoryDto category) {
            this.category = category;
            return this;
        }

        public EventFullDtoBuilder initiator(UserShortDto initiator) {
            this.initiator = initiator;
            return this;
        }

        public EventFullDtoBuilder location(LocationDto location) {
            this.location = location;
            return this;
        }
    }
}