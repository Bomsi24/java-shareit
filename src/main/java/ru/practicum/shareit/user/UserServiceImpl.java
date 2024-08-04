package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
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
        return UserMapper.toUserDto(userRepository.findById(id).orElseThrow(() -> {
            log.error("Пользователь с id:{} не найден", id);
            return new NotFoundException("Пользователь не найден");
        }));
    }

    @Override
    public UserDto create(UserDto userDto) {
        log.info("Начало создания юзера: {}", userDto);
        if (userRepository.existsByEmail(userDto.getEmail())) {
            log.error("Указанна существующая почта");
            throw new ConflictException("Указанная почта уже существует");
        }

        log.info("Создание юзера в репозитории");
        User user = userRepository.save(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto update(int userId, UserDtoUpdate newUser) {
        log.info("Начало процесса обновления юзера");

        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.error("Указанный id пользователя не существует");
            return new ValidationException("Указан несуществующий пользователь");
        });
        if (newUser.getEmail() != null && !newUser.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(newUser.getEmail())) {
                log.error("Указанна существующая почта");
                throw new ConflictException("Указанна существующая почта");
            }
            log.info("обновление почты");
            user.setEmail(newUser.getEmail());
            log.info("почта обновлена");
        }
        if (newUser.getName() != null) {
            user.setName(newUser.getName());
            log.info("имя обновлено");
        }

        userRepository.save(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public void deleteUser(int id) {
        log.info("Начало процесса удаления юзера");
        userRepository.deleteById(id);
        log.info("Юзер успешно удален");
    }

    @Override
    public List<UserDto> getAllUsers() {
        log.info("Начало получения всех юзеров");
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .toList();
    }
}
