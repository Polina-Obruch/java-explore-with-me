package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.core.utils.PaginationUtils;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.request.dto.RequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class PrivateEventController {
    private final EventService eventService;
    private final EventMapper eventMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable Long userId, @Valid @RequestBody EventRequestDto eventRequestDto) {
        log.info("Запрос на создание события - private");
        return eventMapper.eventToEventFullDto(eventService.addEventPrivate(userId, eventMapper.eventRequestDtoToEvent(eventRequestDto)));
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long userId,
                                    @PathVariable Long eventId,
                                    @Valid @RequestBody EventUpdateUserRequestDto updateEvent) {
        log.info("Запрос на обновление события - private");
        return eventMapper.eventToEventFullDto(eventService.updateEventPrivate(userId, eventId, updateEvent));
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventById(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Запрос на выдачу события - private");
        return eventMapper.eventToEventFullDto(eventService.getEventByIdPrivate(userId, eventId));
    }

    @GetMapping
    public List<EventShortDto> getAllEvents(@PathVariable Long userId,
                                            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                            @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Запрос на выдачу списка событий - private");
        return eventMapper.listEventsToListEventShortSto(eventService.getAllEventsPrivate(userId, PaginationUtils.toMakePage(from, size)));
    }

    @PatchMapping("/{eventId}/requests")
    public AnswerStatusUpdateDto updateEventRequests(@PathVariable Long userId, @PathVariable Long eventId,
                                                     @Valid @RequestBody RequestStatusUpdateDto updateRequestDto) {
        return eventService.updateStatusEventRequestPrivate(userId, eventId, updateRequestDto);
    }

    @GetMapping("/{eventId}/requests")
    public List<RequestDto> getEventRequests(@PathVariable Long userId, @PathVariable Long eventId) {
        return eventService.getAllRequestByEventIdPrivate(userId, eventId);
    }
}
