package ru.practicum.ewm.reviews.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.reviews.model.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
