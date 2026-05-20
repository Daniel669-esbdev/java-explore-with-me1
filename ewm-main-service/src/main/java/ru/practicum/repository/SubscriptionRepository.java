package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Subscription;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findByFollowerIdAndFollowingId(Long followerId, Long followingId);

    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);

    List<Subscription> findAllByFollowerId(Long followerId, Pageable pageable);

    List<Subscription> findAllByFollowingId(Long followingId, Pageable pageable);
}