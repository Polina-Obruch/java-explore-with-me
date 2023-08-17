package ru.practicum.ewm.reviews.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.reviews.dto.ReviewDto;
import ru.practicum.ewm.reviews.dto.ReviewRequestDto;
import ru.practicum.ewm.reviews.mapper.ReviewMapper;
import ru.practicum.ewm.reviews.service.ReviewService;
import ru.practicum.ewm.validation.group.Marker;

import javax.validation.Valid;

@Validated
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/review")
public class ReviewController {
    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper;

    @Validated({Marker.OnCreate.class})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ReviewDto addReviewPrivate(@PathVariable Long userId,
                                      @RequestParam Long eventId,
                                      @Valid @RequestBody ReviewRequestDto reviewRequestDto) {
        log.info("Запрос на создание отзыва");
        return reviewMapper.toReviewDto(reviewService.addReviewPrivate(userId, eventId, reviewMapper.toReview(reviewRequestDto)));
    }

    @PatchMapping(path = "/{reviewId}")
    public ReviewDto updateReviewPrivate(@PathVariable Long userId,
                                         @PathVariable Long reviewId,
                                         @Valid @RequestBody ReviewRequestDto reviewRequestDto) {
        log.info("Запрос на обновление отзыва");
        return reviewMapper.toReviewDto(reviewService.updateReviewPrivate(userId, reviewId, reviewMapper.toReview(reviewRequestDto)));
    }

    @GetMapping(path = "/{reviewId}")
    public ReviewDto getReviewPublic(@PathVariable Long userId, @PathVariable Long reviewId) {
        log.info("Запрос на выдачу отзыва");
        return reviewMapper.toReviewDto(reviewService.getReviewPublic(userId, reviewId));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(path = "/{reviewId}")
    public void removeReviewPrivate(@PathVariable Long userId, @PathVariable Long reviewId) {
        log.info("Запрос на удаление отзыва");
        reviewService.removeReviewPrivate(userId, reviewId);
    }
}
