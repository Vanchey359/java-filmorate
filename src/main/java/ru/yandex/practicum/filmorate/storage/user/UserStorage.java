package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    Collection<User> getAll();

    Optional<User> getById(long id);

    User add(User user);

    User update(User user);

    User delete(User user);
}
