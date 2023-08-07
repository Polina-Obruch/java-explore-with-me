package ru.practicum.ewm.user.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.dto.UserRequestDto;
import ru.practicum.ewm.user.dto.UserShortDto;
import ru.practicum.ewm.user.model.User;

import java.util.List;

@Component
@Mapper(componentModel = "spring")
public interface UserMapper {

    User userRequestDtoToUser(UserRequestDto userRequestDto);

    UserDto userToUserDto(User user);

    UserShortDto userToUserShortDto(User user);

    List<UserDto> listUserToListUserDto(List<User> userList);
}
