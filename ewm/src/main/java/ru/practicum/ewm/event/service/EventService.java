package ru.practicum.ewm.event.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.event.dto.AnswerStatusUpdateDto;
import ru.practicum.ewm.event.dto.EventUpdateAdminRequestDto;
import ru.practicum.ewm.event.dto.EventUpdateUserRequestDto;
import ru.practicum.ewm.event.dto.RequestStatusUpdateDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventSort;
import ru.practicum.ewm.event.model.State;
import ru.practicum.ewm.request.dto.RequestDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

//Действия для "владельца" события
public interface EventService {

    Event addEventPrivate(Long userId, Event event);

    Event getEventByIdPrivate(Long userId, Long eventId);

    Event updateEventPrivate(Long userId, Long eventId, EventUpdateUserRequestDto eventUpdate);

    List<Event> getAllEventsPrivate(Long userId, Pageable page);

    List<RequestDto> getAllRequestByEventIdPrivate(Long userId, Long eventId);

    AnswerStatusUpdateDto updateStatusEventRequestPrivate(Long userId, Long eventId, RequestStatusUpdateDto statusUpdateDto);

    Event updateEventAdmin(Long eventId, EventUpdateAdminRequestDto eventUpdate);

    List<Event> getAllEventsAdmin(List<Long> userIds, List<State> states, List<Long> categoryIds,
                                  LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size);

    List<Event> getAllEventsPublic(String text, List<Long> categoryIds, Boolean paid, LocalDateTime rangeStart,
                                   LocalDateTime rangeEnd, boolean onlyAvailable, EventSort sort, int from,
                                   int size, HttpServletRequest request);

    Event getEventByIdPublic(Long eventId, HttpServletRequest request);
}
