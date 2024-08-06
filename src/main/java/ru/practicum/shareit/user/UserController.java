package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoUpdate;

import java.util.List;


@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable Long userId) {
        log.info("Обработка запроса по эндпоинту @GetMapping getUser");
        return userService.getUser(userId);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Обработка запроса по эндпоинту @GetMapping getAllUsers");
        return userService.getAllUsers();
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto user) {
        log.info("Обработка запроса по эндпоинту @PostMapping create");
        return userService.create(user);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable Long userId,
                          @Valid @RequestBody UserDtoUpdate newUser) {
        log.info("Обработка запроса по эндпоинту @PatchMapping update");
        return userService.update(userId, newUser);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.info("Обработка запроса по эндпоинту @DeleteMapping deleteUser");
        userService.deleteUser(userId);
    }
}
