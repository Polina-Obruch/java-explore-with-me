package ru.practicum.ewm.rating.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.event.dto.EventRatingDto;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.rating.service.RatingService;
import ru.practicum.ewm.user.dto.UserRatingDto;
import ru.practicum.ewm.user.mapper.UserMapper;

import javax.validation.constraints.Positive;
import java.util.List;

@Validated
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/rating")
public class RatingController {
    private final RatingService ratingService;
    private final EventMapper eventMapper;
    private final UserMapper userMapper;

    @GetMapping("/events")
    public List<EventRatingDto> getRatingEvents(@Positive @RequestParam(defaultValue = "10") Long numberTop) {
        log.info("Запрос на выдачу рейтинга событий");
        return eventMapper.listEventsToListEventRatingDto(ratingService.getRatingEvents(numberTop));
    }

    @GetMapping("/authors")
    public List<UserRatingDto> getRatingAuthors(@Positive @RequestParam(defaultValue = "10") Long numberTop) {
        log.info("Запрос на выдачу рейтинга авторов событий");
        return userMapper.listUserToListUserRatingDto(ratingService.getRatingInitiatorEvents(numberTop));
    }
}
