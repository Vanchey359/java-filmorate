package ru.yandex.practicum.filmorate.validator;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

@Slf4j
public class UserValidator {

    public static void checkName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Username is empty, login is used as the name");
            user.setName(user.getLogin());
        }
    }

    public static void validate(User user) {
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Login cannot be empty and contain spaces");
        }
    }
}
