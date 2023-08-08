package ru.practicum.ewm.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.core.exception.EntityNotFoundException;
import ru.practicum.ewm.core.exception.ConflictException;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.State;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final UserService userService;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;

    @Transactional
    @Override
    public Request addRequest(Long userId, Long eventId) {
        User user = userService.getUserById(userId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event", eventId));

        //Проверка на повторное участия
        requestRepository.findByRequesterIdAndEventId(userId, eventId).ifPresent(request -> {
            throw new ConflictException("Request can not be sent twice");
        });

        //проверка - инициатор события не может добавить запрос на участие в своём событии
        if (Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ConflictException("Initiator of the event can not send requests to his event");
        }

        //проверка на участие в неопубликованном событии
        if (event.getState() != State.PUBLISHED) {
            throw new ConflictException("Request can not be sent in an unpublished event");
        }

        // проверка - если у события достигнут лимит запросов на участие - необходимо вернуть ошибку
        // ParticipantLimit == 0 - безграничное количество участий
        if (event.getParticipantLimit() != 0 && Objects.equals(
                requestRepository.findCountOfEventConfirmedRequests(eventId), event.getParticipantLimit())) {
            throw new ConflictException("The limit of participants has been exceeded");
        }

        //Статус заявки подтвержден по default
        RequestStatus status = RequestStatus.CONFIRMED;

        if (event.isRequestModeration() && event.getParticipantLimit() != 0) {
            status = RequestStatus.PENDING;
        }

        Request request = Request.builder()
                .event(event)
                .requester(user)
                .created(LocalDateTime.now())
                .status(status)
                .build();

        return requestRepository.save(request);
    }

    @Transactional
    @Override
    public Request cancelRequest(Long userId, Long requestId) {
        userService.getUserById(userId);
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Request", requestId));

        if (!Objects.equals(request.getRequester().getId(), userId)) {
            throw new EntityNotFoundException("Request", requestId);
        }

        request.setStatus(RequestStatus.CANCELED);
        return request;
    }

    @Override
    public List<Request> getRequestsByUserId(Long userId) {
        userService.getUserById(userId);
        return requestRepository.findAllByRequesterId(userId);
    }
}
