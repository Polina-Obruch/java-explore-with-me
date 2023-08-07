package ru.practicum.ewm.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.service.RequestService;

import java.util.List;

@Validated
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class RequestController {
    private final RequestService requestService;
    private final RequestMapper requestMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto addRequest(@PathVariable Long userId, @RequestParam Long eventId) {
        log.info("Запрос на создание заявки на участие в событии");
        return requestMapper.requestToRequestDto(requestService.addRequest(userId, eventId));
    }

    @PatchMapping("/{requestId}/cancel")
    public RequestDto cancelRequest(@PathVariable Long userId, @PathVariable Long requestId) {
        log.info("Запрос на отмену заявки на участие в событии");
        return requestMapper.requestToRequestDto(requestService.cancelRequest(userId, requestId));
    }

    @GetMapping
    public List<RequestDto> getAllRequests(@PathVariable Long userId) {
        log.info("Запрос на выдачу списка заявок на участие текущего пользователя");
        return requestMapper.requestListToRequestDtoList(requestService.getRequestsByUserId(userId));
    }
}
