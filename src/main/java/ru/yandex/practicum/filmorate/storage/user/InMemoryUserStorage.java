package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage{

    private long currentId = 0L;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public List<User> getAll() {
        log.info("Got a list of all users");
        return new ArrayList<>(users.values());
    }

    @Override
    public User getById(long id) throws NotFoundException {
        User user = users.get(id);
        if (user == null) {
            throw new NotFoundException("User with id was not found");
        }
        log.info("Got a user with id = {}", user.getId());
        return user;
    }

    @Override
    public User add(User user) {
        user.setId(++currentId);
        users.put(user.getId(), user);
        log.info("User {} added to the list of all users", user.getName());
        return user;
    }

    @Override
    public User update(User user) throws NotFoundException {
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("User with this id was not found");
        }
        users.put(user.getId(), user);
        log.info("User {} updated", user.getName());
        return user;
    }

    @Override
    public void delete(User user) {
        users.remove(user.getId());
        log.info("User {} has been deleted", user.getName());
    }
}
