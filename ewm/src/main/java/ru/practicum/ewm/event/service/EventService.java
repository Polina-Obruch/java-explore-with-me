package ru.practicum.ewm.event.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.event.model.*;
import ru.practicum.ewm.request.model.AnswerStatusUpdate;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatusUpdate;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;


public interface EventService {

    /**
     * Private - методы предназначены для владельца события
     *
     * @param userId - должен совпадать с INITIATOR_ID в Event
     */
    Event addEventPrivate(Long userId, Event event);

    Event getEventByIdPrivate(Long userId, Long eventId);

    Event updateEventPrivate(Long userId, Long eventId, Event eventUpdate, StateUserAction userAction);

    List<Event> getAllEventsPrivate(Long userId, Pageable page);

    List<Request> getAllRequestByEventIdPrivate(Long userId, Long eventId);

    AnswerStatusUpdate updateStatusEventRequestPrivate(Long userId, Long eventId, RequestStatusUpdate statusUpdate);

    Event updateEventAdmin(Long eventId, Event eventUpdate, StateAdminAction adminAction);

    List<Event> getAllEventsAdmin(List<Long> userIds, List<State> states, List<Long> categoryIds,
                                  LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size);

    List<Event> getAllEventsPublic(String text, List<Long> categoryIds, Boolean paid, LocalDateTime rangeStart,
                                   LocalDateTime rangeEnd, boolean onlyAvailable, EventSort sort, int from,
                                   int size, HttpServletRequest request);

    Event getEventByIdPublic(Long eventId, HttpServletRequest request);
}
