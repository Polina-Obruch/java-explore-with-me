package ru.practicum.ewm.user.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.user.model.User;

import java.util.List;


public interface UserService {
    User addUser(User user);

    void remove(Long userId);

    List<User> getUsersByIds(List<Long> userIds, Pageable page);
}
