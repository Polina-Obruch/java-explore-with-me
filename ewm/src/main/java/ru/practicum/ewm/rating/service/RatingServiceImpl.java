package ru.practicum.ewm.rating.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.reviews.repository.ReviewRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Идея рейтинга - как отдельная страница сайта ввиде топ - 10 мероприятий/авторов
 * Т.е. необходимо будет несколько раз в неделю (например) его обновлять, чтобы учесть прошедшие мероприятия и их оценку
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private static final Double Q = 1.96;

    @Override
    public List<Event> getRatingEvents(Long numberTop) {
        List<Event> events = eventRepository.findEventWithReview();

        List<Long> eventIds = events.stream()
                .map(Event::getId).collect(Collectors.toList());

        Map<Long, Long> countLikes = reviewRepository.findPositiveReviewByEventIdIn(eventIds).stream().collect(
                Collectors.groupingBy(review ->
                        review.getEvent().getId(), Collectors.counting()));

        Map<Long, Long> count = reviewRepository.findAllReviewByEventIdIn(eventIds).stream().collect(
                Collectors.groupingBy(review ->
                        review.getEvent().getId(), Collectors.counting()));

        Map<Long, BigDecimal> ratings = findRating(countLikes, count);

        events.forEach(event -> event.setRating(ratings.getOrDefault(event.getId(), BigDecimal.valueOf(0.0))));

        events.sort(Comparator.comparing(Event::getRating).reversed());

        return events.stream().limit(numberTop).collect(Collectors.toList());
    }

    @Override
    public List<User> getRatingInitiatorEvents(Long numberTop) {
        List<User> users = userRepository.findAllInitiatorEvent();

        List<Long> userIds = users.stream()
                .map(User::getId).collect(Collectors.toList());

        Map<Long, Long> countLikes = reviewRepository.findPositiveReviewByUserIdIn(userIds).stream().collect(
                Collectors.groupingBy(review ->
                        review.getEvent().getInitiator().getId(), Collectors.counting()));

        Map<Long, Long> count = reviewRepository.findAllReviewByUserIdIn(userIds).stream().collect(
                Collectors.groupingBy(review ->
                        review.getEvent().getInitiator().getId(), Collectors.counting()));


        Map<Long, BigDecimal> ratings = findRating(countLikes, count);

        users.forEach(user -> user.setRating(ratings.getOrDefault(user.getId(), BigDecimal.valueOf(0.0))));

        users.sort(Comparator.comparing(User::getRating).reversed());

        return users.stream().limit(numberTop).collect(Collectors.toList());
    }

    /**
     * Расчет рейтинга делаем по нижней границе доверительного интервала Вильсона для параметра Бернулли.
     * Это обеспечит более точную оценку рейтинга при малом количестве оценок.
     * Т.е такой расчет помогает найти баланс между количеством положительных оценок и их общего количества.
     * Q (quantile) - это параметр для настройки точности вычеслений (значение, которое заданная случайная
     * величина не превышает с фиксированной вероятностью);
     * n - общее число оценок;
     * p - доля положительных оценок;
     * Результат переводим в 10 бальную систему и округляем до 1 знака после запятой.
     */
    private Map<Long, BigDecimal> findRating(Map<Long, Long> countLikes, Map<Long, Long> count) {
        Map<Long, BigDecimal> ratings = new HashMap<>();

        for (Map.Entry<Long, Long> entry : count.entrySet()) {
            Long n = entry.getValue();
            Long key = entry.getKey();
            Long countLike = countLikes.get(key);
            double p = 0;

            if (countLike != null) {
                p = 1.0 * countLike / n;
            }

            BigDecimal rating = BigDecimal.valueOf(((p + Q * Q / (2 * n) - Q * Math.sqrt((p * (1 - p) + Q * Q / (4 * n)) / n)) / (1 + Q * Q / n)) * 10);
            ratings.put(key, rating.setScale(1, RoundingMode.HALF_UP));
        }

        return ratings;
    }
}
