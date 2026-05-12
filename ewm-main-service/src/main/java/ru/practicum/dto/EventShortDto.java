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

    public static class EventShortDtoBuilder {
        public EventShortDtoBuilder category(CategoryDto category) {
            this.category = category != null ? CategoryDto.builder().id(category.getId()).name(category.getName()).build() : null;
            return this;
        }

        public EventShortDtoBuilder initiator(UserShortDto initiator) {
            this.initiator = initiator != null ? UserShortDto.builder().id(initiator.getId()).name(initiator.getName()).build() : null;
            return this;
        }
    }
}