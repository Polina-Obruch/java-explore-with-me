package ru.practicum.ewm.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.core.exception.EntityNotFoundException;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Transactional
    @Override
    public User addUser(User user) {
        log.info("Добавление пользователя");
        return userRepository.save(user);
    }

    @Transactional
    @Override
    public void remove(Long userId) {
        log.info(String.format("Удаление пользователя c id = %d", userId));
        userRepository.findById(userId).orElseThrow(()
                -> new EntityNotFoundException("User", userId));
        userRepository.deleteById(userId);
    }

    @Override
    public List<User> getUsersByIds(List<Long> userIds, Pageable page) {
        log.info("Выдача пользователей по списку userIds");
        List<User> users;

        if (userIds == null || userIds.isEmpty()) {
            users = userRepository.findAll(page).toList();
        } else {
            users = userRepository.findAllByIdIn(userIds, page);
        }

        return users;
    }
}
