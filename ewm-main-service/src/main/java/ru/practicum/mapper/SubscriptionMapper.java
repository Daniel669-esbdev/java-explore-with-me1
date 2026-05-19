package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.SubscriptionDto;
import ru.practicum.model.Subscription;
import ru.practicum.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class SubscriptionMapper {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Subscription toSubscription(User follower, User following) {
        return Subscription.builder()
                .follower(follower)
                .following(following)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static SubscriptionDto toSubscriptionDto(Subscription sub) {
        return SubscriptionDto.builder()
                .id(sub.getId())
                .followerId(sub.getFollower().getId())
                .followerName(sub.getFollower().getName())
                .followingId(sub.getFollowing().getId())
                .followingName(sub.getFollowing().getName())
                .createdAt(sub.getCreatedAt().format(FORMATTER))
                .build();
    }
}