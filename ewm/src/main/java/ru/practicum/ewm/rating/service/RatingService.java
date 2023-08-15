package ru.practicum.ewm.rating.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.model.User;

import java.util.List;

public interface RatingService {
    List<Event> getRatingEvents(Pageable page);

    List<User> getRatingInitiatorEvents(Pageable page);
}
