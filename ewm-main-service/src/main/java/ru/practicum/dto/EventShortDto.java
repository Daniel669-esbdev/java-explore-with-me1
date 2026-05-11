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
public class EventShortDto {
    private String annotation;
    private CategoryDto category;
    private Integer confirmedRequests;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private Long id;
    private UserShortDto initiator;
    private Boolean paid;
    private String title;
    private Long views;

    public static class EventShortDtoBuilder {
        private CategoryDto category;
        private UserShortDto initiator;

        public EventShortDtoBuilder category(CategoryDto category) {
            this.category = category;
            return this;
        }

        public EventShortDtoBuilder initiator(UserShortDto initiator) {
            this.initiator = initiator;
            return this;
        }
    }
}