package ru.practicum.ewm.event.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.event.dto.EventUpdateUserRequestDto;
import ru.practicum.ewm.event.model.Event;

import java.util.List;

//Действия для "владельца" события
public interface EventServicePrivate {

    Event addEvent(Long userId, Event event);

    Event getEventById(Long userId, Long eventId);

    Event updateEvent(Long userId, Long eventId, EventUpdateUserRequestDto eventUpdate);

    List<Event> getAllEvents(Long userId, Pageable page);


}
