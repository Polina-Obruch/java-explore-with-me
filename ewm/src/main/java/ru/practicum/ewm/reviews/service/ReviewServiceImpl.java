package ru.practicum.ewm.reviews.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.core.exception.ConflictException;
import ru.practicum.ewm.core.exception.EntityNotFoundException;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.State;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.reviews.model.Review;
import ru.practicum.ewm.reviews.repository.ReviewRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;

    @Transactional
    @Override
    public Review addReviewPrivate(Long userId, Long eventId, Review review) {
        log.info("Добавление отзыва - private");

        //Проверка на существования пользователя нужна здесь для ошибки 404.
        // Т.к. иначе при несуществующем пользователе не будет находиться request, а это уже 409 ошибка
        User user = getUser(userId);
        Event event = getEvent(eventId);

        if (Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ConflictException("Initiator of the event can not send review to his event");
        }

        if (event.getState() != State.PUBLISHED) {
            throw new ConflictException("Can't add review to unpublished event");
        }

        Optional<Request> request = requestRepository.findByRequesterIdAndEventIdAndStatus(
                userId, eventId, RequestStatus.CONFIRMED);
        if (request.isEmpty()) {
            throw new ConflictException("Can't add review without confirmed request");
        }

        //Основная идея - нельзя оставить отзыв на мероприятие, которое даже не началось
        if (review.getCreated().isBefore(event.getEventDate())) {
            throw new ConflictException("Can't add review before start event");
        }

        review.setUser(user);
        review.setEvent(event);
        return reviewRepository.save(review);
    }

    @Transactional
    @Override
    public Review updateReviewPrivate(Long userId, Long reviewId, Review updateReview) {
        log.info(String.format("Обновление отзыва c id = %d - private", reviewId));
        checkIfUserExists(userId);
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Review", reviewId));

        if (updateReview.getIsLike() != null) {
            review.setIsLike(updateReview.getIsLike());
        }

        if (updateReview.getFeedback() != null) {
            review.setFeedback(updateReview.getFeedback());
        }

        return review;
    }

    //Получить отзыв может любой пользователь
    @Override
    public Review getReviewPublic(Long userId, Long reviewId) {
        log.info(String.format("Выдача отзыва c id = %d - private", reviewId));
        checkIfUserExists(userId);

        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Review", reviewId));
    }

    @Transactional
    @Override
    public void removeReviewPrivate(Long userId, Long reviewId) {
        log.info(String.format("Удаление отзыва c id = %d - private", reviewId));
        checkIfUserExists(userId);
        reviewRepository.deleteById(reviewId);
    }

    private void checkIfUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User", userId);
        }
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));
    }

    private Event getEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event", eventId));
    }
}
