package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventUpdateAdminRequestDto;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.State;
import ru.practicum.ewm.event.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
public class AdminEventController {
    private final EventService eventService;
    private final EventMapper eventMapper;

    @PatchMapping("/{eventId}")
    public EventFullDto moderateEvent(@PathVariable Long eventId, @Valid @RequestBody EventUpdateAdminRequestDto updateEventDto) {
        log.info("Запрос на модерацию события - admin");
        return eventMapper.eventToEventFullDto(eventService.updateEventAdmin(
                eventId, eventMapper.eventRequestDtoToEvent(updateEventDto), updateEventDto.getStateAction()));
    }

    @GetMapping
    public List<EventFullDto> getAllEvents(@RequestParam(name = "users", required = false) List<Long> userIds,
                                           @RequestParam(required = false) List<State> states,
                                           @RequestParam(name = "categories", required = false) List<Long> categoryIds,
                                           @RequestParam(required = false) LocalDateTime rangeStart,
                                           @RequestParam(required = false) LocalDateTime rangeEnd,
                                           @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                           @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Запрос на выдачу событий - admin");
        return eventMapper.listEventsToListEventFullDto(eventService.getAllEventsAdmin(userIds, states, categoryIds, rangeStart, rangeEnd, from, size));
    }
}
