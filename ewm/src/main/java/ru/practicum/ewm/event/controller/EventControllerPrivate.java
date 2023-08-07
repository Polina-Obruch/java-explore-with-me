package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.core.mapper.PaginationMapper;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventRequestDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.EventUpdateUserRequestDto;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.service.EventServicePrivate;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class EventControllerPrivate {
    private final EventServicePrivate eventServicePrivate;
    private final EventMapper eventMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable Long userId, @Valid @RequestBody EventRequestDto eventRequestDto) {
        log.info("Запрос на создание события");
        return eventMapper.eventToEventFullDto(eventServicePrivate.addEvent(userId, eventMapper.eventRequestDtoToEvent(eventRequestDto)));
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long userId,
                                    @PathVariable Long eventId,
                                    @Valid @RequestBody EventUpdateUserRequestDto updateEvent) {
        return eventMapper.eventToEventFullDto(eventServicePrivate.updateEvent(userId, eventId, updateEvent));
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventById(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Запрос на выдачу события");
        return eventMapper.eventToEventFullDto(eventServicePrivate.getEventById(userId, eventId));
    }

    @GetMapping
    public List<EventShortDto> getAllEvents(@PathVariable Long userId,
                                            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                            @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Запрос на выдачу списка событий");
        return eventMapper.listEventsToListEventShortSto(eventServicePrivate.getAllEvents(userId, PaginationMapper.toMakePage(from, size)));
    }
}
