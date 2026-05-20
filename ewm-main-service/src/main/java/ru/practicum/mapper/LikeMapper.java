package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.RatingDto;
import ru.practicum.model.Event;

@UtilityClass
public class LikeMapper {

    public static RatingDto toRatingDto(Event event, Long likes, Long dislikes) {
        return RatingDto.builder()
                .eventId(event.getId())
                .eventTitle(event.getTitle())
                .likesCount(likes)
                .dislikesCount(dislikes)
                .totalRating(likes - dislikes)
                .build();
    }
}