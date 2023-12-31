package ru.practicum.ewm.event.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.core.exception.EntityNotFoundException;
import ru.practicum.ewm.core.exception.ConflictException;
import ru.practicum.ewm.core.exception.ValidationException;
import ru.practicum.ewm.event.model.*;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.repository.LocationRepository;
import ru.practicum.ewm.request.model.AnswerStatusUpdate;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.request.model.RequestStatusUpdate;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.CreateEndpointHitDto;
import ru.practicum.stats.dto.ViewStatsDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private static final String APP_NAME = "ewm";
    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final StatsClient statsClient;

    @Transactional
    @Override
    public Event addEventPrivate(Long userId, Event event) {
        log.info("Добавление события - private");
        User user = userRepository.findById(userId).orElseThrow(()
                -> new EntityNotFoundException("User", userId));

        Category category = categoryRepository.findById(event.getCategory().getId())
                .orElseThrow(() -> new EntityNotFoundException("Category", event.getCategory().getId()));

        checkTimeValidationPrivate(event.getEventDate());

        // Установка default значений
        if (event.getPaid() == null) {
            event.setPaid(false);
        }

        if (event.getParticipantLimit() == null) {
            event.setParticipantLimit(0);
        }

        if (event.getRequestModeration() == null) {
            event.setRequestModeration(true);
        }

        event.setInitiator(user);
        event.setCategory(category);
        event.setState(State.PENDING);
        locationRepository.save(event.getLocation());
        event.setCreatedOn(LocalDateTime.now());
        return eventRepository.save(event);
    }

    @Override
    public Event getEventByIdPrivate(Long userId, Long eventId) {
        log.info(String.format("Выдача события c id = %d - private", eventId));
        checkIfUserExists(userId);
        Event event = getEvent(eventId);

        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            throw new EntityNotFoundException("Event", eventId);
        }
        // тут добавляем статистику просмотров
        this.setViewsAndConfirmedRequest(List.of(event));
        return event;
    }

    @Transactional
    @Override
    public Event updateEventPrivate(Long userId, Long eventId, Event eventUpdate, StateUserAction stateAction) {
        log.info(String.format("Обновление события c id = %d - private", eventId));
        checkIfUserExists(userId);
        Event event = getEvent(eventId);

        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            throw new EntityNotFoundException("Event", eventId);
        }

        // Обновление всех возможных полей
        this.updateEvent(event, eventUpdate);

        LocalDateTime updateTime = eventUpdate.getEventDate();
        if (updateTime != null) {
            checkTimeValidationPrivate(updateTime);
            event.setEventDate(updateTime);
        }

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

        this.setViewsAndConfirmedRequest(List.of(event));

        return event;
    }

    @Override
    public List<Event> getAllEventsPrivate(Long userId, Pageable page) {
        log.info(String.format("Выдача событий владельца c id = %d - private", userId));
        checkIfUserExists(userId);
        List<Event> events = eventRepository.findAllByInitiatorId(userId, page);
        this.setViewsAndConfirmedRequest(events);
        return events;
    }

    @Override
    public List<Request> getAllRequestByEventIdPrivate(Long userId, Long eventId) {
        log.info(String.format("Выдача заявок на участие в мероприятии (eventId = %d) " +
                "владельца c id = %d - private", eventId, userId));
        checkIfUserExists(userId);
        if (!eventRepository.existsById(eventId)) {
            throw new EntityNotFoundException("Event", eventId);
        }
        return requestRepository.findAllByEventIdAndEventInitiatorId(eventId, userId);
    }

    @Transactional
    @Override
    public AnswerStatusUpdate updateStatusEventRequestPrivate(Long userId, Long eventId, RequestStatusUpdate statusUpdate) {
        log.info(String.format("Подтверждение/Отклонение заявок на участие в мероприятии (eventId = %d) " +
                "владельца c id = %d - private", eventId, userId));
        checkIfUserExists(userId);
        Event event = getEvent(eventId);

        AtomicReference<Integer> countConfirmedRequests = new AtomicReference<>(requestRepository.findCountOfEventConfirmedRequests(eventId));
        // ParticipantLimit == 0 - безграничное количество участий
        Integer participantLimit = event.getParticipantLimit();

        // проверка - если у события достигнут лимит запросов на участие - необходимо вернуть ошибку
        if (participantLimit != 0 && countConfirmedRequests.get() >= participantLimit) {
            throw new ConflictException("The limit of participants has been exceeded");
        }

        List<Request> requests = requestRepository.getRequestsForUpdating(eventId, userId, statusUpdate.getRequestIds());

        requests.forEach(request -> {
            if (request.getStatus() != RequestStatus.PENDING) {
                throw new ConflictException("Only pending request can be changed");
            }
        });

        if (statusUpdate.getStatus() == RequestStatus.REJECTED) {
            requests.forEach(request -> request.setStatus(RequestStatus.REJECTED));
            return new AnswerStatusUpdate(Collections.emptyList(), requests);
        }

        List<Request> confirmedRequests = new ArrayList<>();
        List<Request> rejectedRequests = new ArrayList<>();

        requests.forEach(request -> {
            if (countConfirmedRequests.get() < participantLimit) {
                request.setStatus(RequestStatus.CONFIRMED);
                confirmedRequests.add(request);
                // во время цикла должно быть изменение count иначе можем подтвердить больше заявок
                countConfirmedRequests.getAndSet(countConfirmedRequests.get() + 1);
            } else {
                request.setStatus(RequestStatus.REJECTED);
                rejectedRequests.add(request);
            }
        });

        return new AnswerStatusUpdate(confirmedRequests, rejectedRequests);
    }

    @Transactional
    @Override
    public Event updateEventAdmin(Long eventId, Event eventUpdate, StateAdminAction stateAction) {
        log.info(String.format("Обновление события c id = %d - admin", eventId));
        Event event = getEvent(eventId);

        //Обновление всех возможных полей
        this.updateEvent(event, eventUpdate);

        LocalDateTime updateTime = eventUpdate.getEventDate();
        if (updateTime != null) {
            checkTimeValidationAdmin(updateTime);
            event.setEventDate(updateTime);
        }

        if (stateAction != null) {
            switch (stateAction) {
                case PUBLISH_EVENT:
                    if (event.getState() == State.CANCELED) {
                        throw new ConflictException("Cancelled events can not be published");
                    }
                    event.setState(State.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                case REJECT_EVENT:
                    event.setState(State.CANCELED);
                    break;
                default:
                    throw new ConflictException("Not found this action " + stateAction);
            }
        }

        //статистика
        this.setViewsAndConfirmedRequest(List.of(event));
        return event;
    }

    @Override
    public List<Event> getAllEventsAdmin(List<Long> userIds, List<State> states, List<Long> categoryIds,
                                         LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
        log.info("Выдача списка событий - admin");
        List<Event> events = eventRepository.findAllByAdminFilters(userIds, states, categoryIds, rangeStart, rangeEnd, from, size);
        // добавка статистики
        this.setViewsAndConfirmedRequest(events);
        return events;
    }

    @Override
    public List<Event> getAllEventsPublic(String text, List<Long> categoryIds, Boolean paid, LocalDateTime rangeStart,
                                          LocalDateTime rangeEnd, boolean onlyAvailable, EventSort sort, int from,
                                          int size, HttpServletRequest request) {
        log.info("Выдача списка событий - public");
        this.sendStatistic(request);

        if (rangeStart != null && rangeEnd != null) {
            if (rangeEnd.isBefore(rangeStart)) {
                throw new ValidationException("Дата окончания события не может быть раньше начала");
            }
        }

        List<Event> events = eventRepository.findAllByPublicFilters(text, categoryIds, paid, rangeStart, rangeEnd, sort, from, size);

        // добавка статистики
        this.setViewsAndConfirmedRequest(events);

        if (onlyAvailable) {
            events = events.stream()
                    .filter(event -> event.getParticipantLimit() <= event.getConfirmedRequests())
                    .collect(Collectors.toList());
        }

        // сортировка по просмотрам если надо
        if (sort == EventSort.VIEWS) {
            events.sort((event1, event2) -> Long.compare(event2.getViews(), event1.getViews()));
        }
        return events;
    }

    @Override
    public Event getEventByIdPublic(Long eventId, HttpServletRequest request) {
        log.info(String.format("Выдача события c id = %d - public", eventId));
        this.sendStatistic(request);
        Event event = getEvent(eventId);

        if (event.getState() != State.PUBLISHED) {
            throw new EntityNotFoundException("Event", eventId);
        }
        // добавка статистики
        this.setViewsAndConfirmedRequest(List.of(event));
        return event;
    }


    private void updateEvent(Event event, Event eventUpdate) {
        if (event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }

        if (eventUpdate.getAnnotation() != null && !eventUpdate.getAnnotation().isBlank()) {
            event.setAnnotation(eventUpdate.getAnnotation());
        }

        Long categoryId = eventUpdate.getCategory().getId();
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new EntityNotFoundException("Category", categoryId));
            event.setCategory(category);
        }

        if (eventUpdate.getTitle() != null && !eventUpdate.getTitle().isBlank()) {
            event.setTitle(eventUpdate.getTitle());
        }

        if (eventUpdate.getDescription() != null && !eventUpdate.getDescription().isBlank()) {
            event.setDescription(eventUpdate.getDescription());
        }

        Location location = eventUpdate.getLocation();
        if (location != null) {
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
    }

    private void checkIfUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User", userId);
        }
    }

    private Event getEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event", eventId));
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

    private void sendStatistic(HttpServletRequest request) {
        statsClient.addEndpointHit(new CreateEndpointHitDto(
                APP_NAME,
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now()
        ));
    }

    private void setViewsAndConfirmedRequest(List<Event> events) {
        log.info("Добавление подтвержденных заявок");
        //Вроде бы тут сразу группировка без промежуточного списка:
        // 1-й stream создание списка для самого запроса в БД
        // 2-й stream сразу же получение результата с группировкой
        Map<Long, Long> countRequestMap = requestRepository.findAllConfirmedRequestsByEventIdIn(
                        events.stream().map(Event::getId).collect(Collectors.toList()))
                .stream().collect(Collectors.groupingBy(request ->
                        request.getEvent().getId(), Collectors.counting()));

        events.forEach(event -> event.setConfirmedRequests((
                countRequestMap.getOrDefault(event.getId(), 0L))));

        log.info("Добавление просмотров");
        Map<String, Event> eventsMap = events.stream()
                .collect(Collectors.toMap(event -> "/events/" + event.getId(), event -> event));

        ObjectMapper objectMapper = new ObjectMapper();

        LocalDateTime minStart = LocalDateTime.now().minusYears(10);
        LocalDateTime maxEnd = LocalDateTime.now().plusYears(10);

        Object rawStatistics = statsClient.getStatistics(minStart,
                maxEnd, new ArrayList<>(eventsMap.keySet()), true).getBody();

        List<ViewStatsDto> viewStatsList = objectMapper.convertValue(rawStatistics, new TypeReference<>() {
        });

        viewStatsList.forEach(viewStatsDto -> {
            if (eventsMap.containsKey(viewStatsDto.getUri())) {
                eventsMap.get(viewStatsDto.getUri()).setViews(viewStatsDto.getHits());
            }
        });
    }
}