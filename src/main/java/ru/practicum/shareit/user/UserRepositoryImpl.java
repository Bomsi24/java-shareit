package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class UserRepositoryImpl implements UserRepository {
    private final Map<Integer, User> users = new HashMap<>();
    private int lastUserId;

    @Override
    public User getUser(int userId) {
        log.info("Получение юзера из репозитория. id{}", userId);
        return users.get(userId);
    }

    @Override
    public User createUser(User user) {
        log.info("Добавление юзера: {} в репозиторий", user.getId());
        user.setId(++lastUserId);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(int userId, User user) {
        log.info("Обновление юзера: {} в репозитории", userId);
        users.put(userId, user);
        return users.get(userId);
    }

    @Override
    public void deleteUser(int userId) {
        log.info("Удаление юзера: {} в репозитории", userId);
        users.remove(userId);
    }

    @Override
    public boolean userExists(int userId) {
        log.info("Проверка наличия юзера");
        return users.containsKey(userId);
    }

    @Override
    public List<User> getAllUsers() {
        log.info("Получение всех юзеров из репозитория");
        return new ArrayList<>(users.values());
    }
}
