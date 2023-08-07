package ru.practicum.ewm.request.service;

import ru.practicum.ewm.request.model.Request;

import java.util.List;

public interface RequestService {
    Request addRequest(Long userId, Long eventId);

    Request cancelRequest(Long userId, Long requestId);

    List<Request> getRequestsByUserId(Long userId);
}
