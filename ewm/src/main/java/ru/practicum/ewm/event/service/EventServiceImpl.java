package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.service.CategoryService;
import ru.practicum.ewm.core.exception.EntityNotFoundException;
import ru.practicum.ewm.core.exception.ConflictException;
import ru.practicum.ewm.core.exception.ValidationException;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.mapper.LocationMapper;
import ru.practicum.ewm.event.model.*;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.repository.LocationRepository;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final RequestRepository requestRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final LocationMapper locationMapper;

    @Transactional
    @Override
    public Event addEventPrivate(Long userId, Event event) {
        log.info("Добавление события");
        User user = userService.getUserById(userId);
        Category category = categoryService.getCategoryById(event.getCategory().getId());

        checkTimeValidation(event.getEventDate());

        event.setInitiator(user);
        event.setCategory(category);
        event.setState(State.PENDING);
        locationRepository.save(event.getLocation());
        event.setCreatedOn(LocalDateTime.now());
        return eventRepository.save(event);
    }

    @Override
    public Event getEventByIdPrivate(Long userId, Long eventId) {
        log.info(String.format("Выдача события c id = %d", eventId));
        userService.getUserById(userId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event", eventId));

        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            throw new EntityNotFoundException("Event", eventId);
        }
        // тут добавляем статистику просмотров

        return event;
    }

    @Transactional
    @Override
    public Event updateEventPrivate(Long userId, Long eventId, EventUpdateUserRequestDto eventUpdate) {
        log.info(String.format("Обновление события c id = %d", eventId));
        userService.getUserById(userId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event", eventId));

        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            throw new EntityNotFoundException("Event", eventId);
        }

        if (event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }

        LocalDateTime updateTime = eventUpdate.getEventDate();
        if (updateTime != null) {
            checkTimeValidation(updateTime);
            event.setEventDate(updateTime);
        }

        if (eventUpdate.getAnnotation() != null) {
            event.setAnnotation(eventUpdate.getAnnotation());
        }

        Long categoryId = eventUpdate.getCategory();
        if (categoryId != null) {
            Category category = categoryService.getCategoryById(categoryId);
            event.setCategory(category);
        }

        if (eventUpdate.getTitle() != null) {
            event.setTitle(eventUpdate.getTitle());
        }

        if (eventUpdate.getDescription() != null) {
            event.setDescription(eventUpdate.getDescription());
        }

        LocationDto locationDto = eventUpdate.getLocation();
        if (locationDto != null) {
            Location location = locationMapper.locationDtoToLocation(locationDto);
            locationRepository.save(location);
            event.setLocation(location);
        }

        if (eventUpdate.getPaid() != null) {
            event.setPaid(eventUpdate.getPaid());
        }

        if (eventUpdate.getParticipantLimit() != null) {
            event.setParticipantLimit(eventUpdate.getParticipantLimit());
        }

        if (eventUpdate.getRequestModeration() != null) {
            event.setRequestModeration(eventUpdate.getRequestModeration());
        }

        StateUserAction stateAction = eventUpdate.getStateAction();
        if (stateAction != null) {
            if (stateAction.equals(StateUserAction.SEND_TO_REVIEW)) {
                event.setState(State.PENDING);
            } else {
                event.setState(State.CANCELED);
            }
        }

        //добавка статистики

        return event;
    }

    @Override
    public List<Event> getAllEventsPrivate(Long userId, Pageable page) {
        //нужна ли проверка на существования пользователя
        // тут добавляем статистику просмотров
        return eventRepository.findAllByInitiatorId(userId, page);
    }

    @Override
    public List<Request> getAllRequestByEventIdPrivate(Long userId, Long eventId) {
        userService.getUserById(userId);
        eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event", eventId));
        return requestRepository.findAllByEventIdAndEventInitiatorId(eventId, userId);
    }

    @Transactional
    @Override
    public AnswerStatusUpdateDto updateStatusEventRequestPrivate(Long userId, Long eventId, RequestStatusUpdateDto statusUpdateDto) {
        return null;
    }

    @Transactional
    @Override
    public Event moderateEventAdmin(Long eventId, EventUpdateAdminRequestDto eventUpdate) {
        return null;
    }

    @Override
    public List<Event> getAllEventsAdmin(List<Long> userIds, List<State> states, List<Long> categoryIds,
                                         LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
        return null;
    }

    @Override
    public List<Event> getEventsPublic(String text, List<Long> categoryIds, Boolean paid, LocalDateTime rangeStart,
                                       LocalDateTime rangeEnd, boolean onlyAvailable, EventSort sort, int from,
                                       int size, HttpServletRequest request) {
        return null;
    }

    @Override
    public Event getEventByIdPublic(Long eventId, HttpServletRequest request) {
        return null;
    }

    private void checkTimeValidation(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now().minusHours(2))) {
            throw new ValidationException("Field: eventDate. Error: должно содержать дату, которая еще не наступила.");
        }
    }
}
