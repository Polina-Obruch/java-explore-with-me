package ru.practicum.ewm.reviews.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.reviews.dto.ReviewDto;
import ru.practicum.ewm.reviews.dto.ReviewRequestDto;
import ru.practicum.ewm.reviews.model.Review;

@Component
@Mapper(componentModel = "spring")
public interface ReviewMapper {

    ReviewDto toReviewDto(Review review);

    Review toReview(ReviewRequestDto reviewRequestDto);
}
