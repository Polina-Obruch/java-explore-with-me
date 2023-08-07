package ru.practicum.ewm.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.core.mapper.PaginationMapper;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.dto.UserRequestDto;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@Valid @RequestBody UserRequestDto userRequestDto) {
        log.info("Запрос на создание пользователя");
        return userMapper.userToUserDto(userService.addUser(userMapper.userRequestDtoToUser(userRequestDto)));
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeUser(@PathVariable Long userId) {
        log.info("Запрос на удаление пользователя");
        userService.remove(userId);
    }

    @GetMapping
    public List<UserDto> getAllUsers(@RequestParam(name = "ids", required = false) List<Long> userIds,
                                     @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                     @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Запрос на выдачу пользователей");
        return userMapper.listUserToListUserDto(userService.getUsersByIds(
                userIds, PaginationMapper.toMakePage(from, size)));
    }
}
