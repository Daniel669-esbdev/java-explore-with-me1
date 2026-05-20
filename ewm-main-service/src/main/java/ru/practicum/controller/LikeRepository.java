package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Like;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByEventIdAndUserId(Long eventId, Long userId);

    void deleteByEventIdAndUserId(Long eventId, Long userId);

    @Query("SELECT COUNT(l) FROM Like l WHERE l.event.id = :eventId AND l.isLike = true")
    Long countLikesByEventId(Long eventId);

    @Query("SELECT COUNT(l) FROM Like l WHERE l.event.id = :eventId AND l.isLike = false")
    Long countDislikesByEventId(Long eventId);
}