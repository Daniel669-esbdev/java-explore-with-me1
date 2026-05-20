package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RatingDto {
    private Long eventId;
    private String eventTitle;
    private Long likesCount;
    private Long dislikesCount;
    private Long totalRating;
}