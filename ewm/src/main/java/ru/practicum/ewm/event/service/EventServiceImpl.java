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
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.service.UserService;
//import ru.practicum.stats.client.StatsClient;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    private final RequestMapper requestMapper;

//    @Value("${ewm-app}")
//    private final String appName;
//    private final StatsClient statsClient;

    @Transactional
    @Override
    public Event addEventPrivate(Long userId, Event event) {
        log.info("Добавление события");
        User user = userService.getUserById(userId);
        Category category = categoryService.getCategoryById(event.getCategory().getId());

        checkTimeValidationPrivate(event.getEventDate());

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

        // Обновление всех возможных полей
        event = this.updateEvent(event, eventUpdate);

        LocalDateTime updateTime = eventUpdate.getEventDate();
        if (updateTime != null) {
            checkTimeValidationPrivate(updateTime);
            event.setEventDate(updateTime);
        }

        StateUserAction stateAction = eventUpdate.getStateAction();

        if (stateAction != null) {
            switch (stateAction) {
                case SEND_TO_REVIEW:
                    event.setState(State.PENDING);
                    break;
                case CANCEL_REVIEW:
                    event.setState(State.CANCELED);
                    break;
                default:
                    throw new ConflictException("Not found this action " + stateAction);
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
    public List<RequestDto> getAllRequestByEventIdPrivate(Long userId, Long eventId) {
        userService.getUserById(userId);
        eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event", eventId));
        return requestMapper.requestListToRequestDtoList(
                requestRepository.findAllByEventIdAndEventInitiatorId(eventId, userId));
    }

    @Transactional
    @Override
    public AnswerStatusUpdateDto updateStatusEventRequestPrivate(Long userId, Long eventId, RequestStatusUpdateDto statusUpdateDto) {
        userService.getUserById(userId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event", eventId));

        Integer countConfirmedRequests = requestRepository.findCountOfEventConfirmedRequests(eventId);
        // ParticipantLimit == 0 - безграничное количество участий
        Integer participantLimit = event.getParticipantLimit();

        // проверка - если у события достигнут лимит запросов на участие - необходимо вернуть ошибку
        if (participantLimit != 0 && Objects.equals(countConfirmedRequests, participantLimit)) {
            throw new ConflictException("The limit of participants has been exceeded");
        }

        List<Request> requests = requestRepository.getRequestsForUpdating(eventId, userId, statusUpdateDto.getRequestIds());

        requests.forEach(request -> {
            if (request.getStatus() != RequestStatus.PENDING) {
                throw new ConflictException("Only pending request can be changed");
            }
        });

        if (statusUpdateDto.getStatus() == RequestStatus.REJECTED) {
            requests.forEach(request -> request.setStatus(RequestStatus.REJECTED));
            List<RequestDto> rejectedRequests = requests.stream().map(requestMapper::requestToRequestDto).collect(Collectors.toList());

            return new AnswerStatusUpdateDto(Collections.emptyList(), rejectedRequests);
        }

        List<RequestDto> confirmedRequests = new ArrayList<>();
        List<RequestDto> rejectedRequests = new ArrayList<>();

        requests.forEach(request -> {
            if (countConfirmedRequests < participantLimit) {
                request.setStatus(RequestStatus.CONFIRMED);
                confirmedRequests.add(requestMapper.requestToRequestDto(request));
            } else {
                request.setStatus(RequestStatus.REJECTED);
                rejectedRequests.add(requestMapper.requestToRequestDto(request));
            }
        });

        return new AnswerStatusUpdateDto(confirmedRequests, rejectedRequests);
    }

    @Transactional
    @Override
    public Event updateEventAdmin(Long eventId, EventUpdateAdminRequestDto eventUpdate) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event", eventId));

        //Обновление всех возможных полей
        event = this.updateEvent(event, eventUpdate);

        LocalDateTime updateTime = eventUpdate.getEventDate();
        if (updateTime != null) {
            checkTimeValidationAdmin(updateTime);
            event.setEventDate(updateTime);
        }

        StateAdminAction stateAction = eventUpdate.getStateAction();

        if (stateAction != null) {
            switch (stateAction) {
                case PUBLISH_EVENT:
                    //Нужно ли тут время публикации
                    if (event.getState() == State.CANCELED) {
                        throw new ConflictException("Cancelled events can not be published");
                    }
                    event.setState(State.PUBLISHED);
                    break;
                case REJECT_EVENT:
                    event.setState(State.CANCELED);
                    break;
                default:
                    throw new ConflictException("Not found this action " + stateAction);
            }
        }

        //статистика
        return event;
    }

    @Override
    public List<Event> getAllEventsAdmin(List<Long> userIds, List<State> states, List<Long> categoryIds,
                                         LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
        List<Event> events = eventRepository.findAllByAdminFilters(userIds, states, categoryIds, rangeStart, rangeEnd, from, size);
        // добавка статистики
        return events;
    }

    @Override
    public List<Event> getAllEventsPublic(String text, List<Long> categoryIds, Boolean paid, LocalDateTime rangeStart,
                                          LocalDateTime rangeEnd, boolean onlyAvailable, EventSort sort, int from,
                                          int size, HttpServletRequest request) {
        //отправка статистики
        if (rangeStart != null && rangeEnd != null) {
            if (rangeEnd.isBefore(rangeStart)) {
                throw new ValidationException("Дата окончания события не может быть раньше начала");
            }
        }

        List<Event> events = eventRepository.findAllByPublicFilters(text, categoryIds, paid, rangeStart, rangeEnd, sort, from, size);

        // добавка статистики
        if (onlyAvailable) {
            events = events.stream()
                    .filter(event -> event.getParticipantLimit() <= event.getConfirmedRequests())
                    .collect(Collectors.toList());
        }

        // сортировка по просмотрам если надо
        return events;
    }

    @Override
    public Event getEventByIdPublic(Long eventId, HttpServletRequest request) {
        //отправка статистики
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event", eventId));

        if (event.getState() != State.PUBLISHED) {
            throw new EntityNotFoundException("Event", eventId);
        }
        // добавка статистики
        return event;
    }


    private Event updateEvent(Event event, EventUpdateDto eventUpdate) {
        if (event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Only pending or canceled events can be changed");
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

        return event;
    }

    private void checkTimeValidationPrivate(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Field: eventDate. Error: должно содержать дату, которая еще не наступила.");
        }
    }

    private void checkTimeValidationAdmin(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ValidationException("Field: eventDate. Error: должно содержать дату, которая еще не наступила.");
        }
    }
}
