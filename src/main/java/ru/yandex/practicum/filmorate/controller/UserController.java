package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final List<User> users = new ArrayList<>();
    private int idCounter = 0;

    @GetMapping
    public List<User> findAll() {
        log.info("Получен список всех пользователей");
        return users;
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        validate(user);
        user.setId(++idCounter);
        users.add(user);
        log.info("Пользователь {} добавлен в список всех пользователей", user.getName());
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        for (User us : users) {
            if (us.getId() == user.getId()) {
                users.remove(us);
                users.add(user);
                log.info("Пользователь {} обновлен", user.getName());
            } else {
                log.warn("Пользователь с таким id не найден");
                throw new ValidationException("User with this id was not found");
            }
        }
        return user;
    }

    public void validate(User user) {
        if (user.getEmail() == null || !user.getEmail().contains("@") || user.getEmail().isBlank()) {
            log.warn("Запрос на действие с пользователем емеил которого пустой или не содержит @");
            throw new ValidationException("Email cannot be empty and must contain the @ symbol");
        }

        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.warn("Запрос на действие с пользователем в логин которого пустой или в нем содержаться пробелы");
            throw new ValidationException("Login cannot be empty and contain spaces");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Имя пользователя пустое, в качестве имени используется логин");
            user.setName(user.getLogin());
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Запрос на действие с пользователем дата рождения которого в будущем");
            throw new ValidationException("Date of birth cannot be in the future");
        }
    }
}
