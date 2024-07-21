package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoUpdate;

import java.util.List;

public interface UserService {
    UserDto getUser(int id);

    List<UserDto> getAllUsers();

    UserDto create(UserDto userDto);

    UserDto update(int userId, UserDtoUpdate userDto);

    void deleteUser(int id);
}
