package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.InternalServerException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoUpdate;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto getUser(int id) {
        log.info("Начало получения юзера");
        return UserMapper.toUserDto(userRepository.getUser(id));
    }

    @Override
    public UserDto create(UserDto userDto) {
        log.info("Начало создания юзера: {}", userDto);
        if (checkEmail(userDto.getEmail())) {
            log.error("Указанна существующая почта");
            throw new InternalServerException("Указанная почта уже существует");
        }

        User user = userRepository.createUser(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto update(int userId, UserDtoUpdate newUser) {
        log.info("Начало процесса обновления юзера");
        if (!userRepository.userExists(userId)) {
            log.error("Указанный id пользователя не существует");
            throw new ValidationException("Указан несуществующий пользователь");
        }
        User user = userRepository.getUser(userId);
        if (newUser.getEmail() != null && !newUser.getEmail().equals(user.getEmail())) {
            if (checkEmail(newUser.getEmail())) {
                log.error("Указанна существующая почта");
                throw new InternalServerException("Указанна существующая почта");
            }
            userRepository.removeEmail(user.getEmail());
            user.setEmail(newUser.getEmail());
            log.info("почта обновлена");
        }
        if (newUser.getName() != null) {
            user.setName(newUser.getName());
            log.info("имя обновлено");
        }

        userRepository.update(userId, user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public void deleteUser(int id) {
        log.info("Начало процесса удаления юзера");
        userRepository.deleteUser(id);
        log.info("Юзер успешно удален");
    }

    @Override
    public List<UserDto> getAllUsers() {
        log.info("Начало получения всех юзеров");
        return userRepository.getAllUsers().stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    private boolean checkEmail(String email) {
        log.info("Запуск проверки на существование почты");
        return userRepository.getUserEmail().contains(email);
    }
}
