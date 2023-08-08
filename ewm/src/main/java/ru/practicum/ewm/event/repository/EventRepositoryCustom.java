package ru.practicum.ewm.event.repository;

import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventSort;
import ru.practicum.ewm.event.model.State;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepositoryCustom {
    List<Event> findAllByAdminFilters(List<Long> userIds,
                                      List<State> states,
                                      List<Long> categoryIds,
                                      LocalDateTime rangeStart,
                                      LocalDateTime rangeEnd,
                                      int from, int size);

    List<Event> findAllByPublicFilters(String text,
                                       List<Long> categoryIds,
                                       Boolean paid,
                                       LocalDateTime rangeStart,
                                       LocalDateTime rangeEnd,
                                       EventSort sort,
                                       int from,
                                       int size);
}
