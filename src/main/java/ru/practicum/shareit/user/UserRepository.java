package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User getUser(int userId);

    User createUser(User user);

    User update(int userId, User user);

    void deleteUser(int userId);

    boolean userExists(int userId);

    List<User> getAllUsers();
}
