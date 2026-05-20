package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
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

    public EventShortDto(String annotation, CategoryDto category, Integer confirmedRequests,
                         LocalDateTime eventDate, Long id, UserShortDto initiator,
                         Boolean paid, String title, Long views) {
        this.annotation = annotation;
        this.category = category != null ? new CategoryDto(category.getId(), category.getName()) : null;
        this.confirmedRequests = confirmedRequests;
        this.eventDate = eventDate;
        this.id = id;
        this.initiator = initiator != null ? new UserShortDto(initiator.getId(), initiator.getName()) : null;
        this.paid = paid;
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

    public static class EventShortDtoBuilder {
        public EventShortDtoBuilder category(CategoryDto category) {
            this.category = category != null ? new CategoryDto(category.getId(), category.getName()) : null;
            return this;
        }

        public EventShortDtoBuilder initiator(UserShortDto initiator) {
            this.initiator = initiator != null ? new UserShortDto(initiator.getId(), initiator.getName()) : null;
            return this;
        }
    }
}