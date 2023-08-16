package ru.practicum.ewm.reviews.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.reviews.model.Review;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT r FROM Review r " +
            "WHERE r.event.initiator.id IN :userIds AND r.isLike = true ")
    List<Review> findPositiveReviewByUserIdIn(List<Long> userIds);

    @Query("SELECT r FROM Review r " +
            "WHERE r.event.id IN :eventIds AND r.isLike = true ")
    List<Review> findPositiveReviewByEventIdIn(List<Long> eventIds);

    @Query("SELECT r FROM Review r " +
            "WHERE r.event.initiator.id IN :userIds ")
    List<Review> findAllReviewByUserIdIn(List<Long> userIds);

    @Query("SELECT r FROM Review r " +
            "WHERE r.event.id IN :eventIds ")
    List<Review> findAllReviewByEventIdIn(List<Long> eventIds);
}

