package ru.yandex.practicum.filmorate.controller;

import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();
    private int idCounter = 0;

    @GetMapping
    public List<User> findAll() {
        log.info("Got a list of all users");
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        validate(user);
        checkName(user);
        user.setId(++idCounter);
        users.put(user.getId(), user);
        log.info("User {} added to the list of all users", user.getName());
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (users.containsKey(user.getId())) {
            checkName(user);
            users.put(user.getId(), user);
            log.info("User {} updated", user.getName());
        } else {
            throw new ValidationException("User with this id was not found");
        }
        return user;
    }

    private void checkName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Username is empty, login is used as the name");
            user.setName(user.getLogin());
        }
    }

    private void validate(User user) {
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Login cannot be empty and contain spaces");
        }
    }
}
