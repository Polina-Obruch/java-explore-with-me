package ru.practicum.ewm.rating.service;

import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.model.User;

import java.util.List;

public interface RatingService {
    List<Event> getRatingEvents(Long numberTop);

    List<User> getRatingInitiatorEvents(Long numberTop);
}
