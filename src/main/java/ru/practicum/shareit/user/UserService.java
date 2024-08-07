package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoUpdate;

import java.util.List;

public interface UserService {
    UserDto getUser(Long id);

    List<UserDto> getAllUsers();

    UserDto create(UserDto userDto);

    UserDto update(Long userId, UserDtoUpdate userDto);

    void deleteUser(Long id);
}
