package ru.practicum.ewm.reviews.service;

import ru.practicum.ewm.reviews.model.Review;

public interface ReviewService {
    Review addReviewPrivate(Long userId, Long eventId, Review review);

    Review updateReviewPrivate(Long userId, Long reviewId, Review updateReview);

    Review getReviewPrivate(Long userId, Long reviewId);

    void removeReviewPrivate(Long userId, Long reviewId);
}
