package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.FilmValidator;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class FilmorateApplicationTests {



    @Test
    void createFilmShouldValidateFilmWithOldReleaseDate() {
        Film film = new Film(1, "Test", "TestTest", LocalDate.of(1300, 11, 12), 45);
        Exception exception = assertThrows(ValidationException.class, () -> FilmValidator.validate(film));
        assertEquals("Release date must not be earlier than 28.12.1895", exception.getMessage());
    }

    @Test
    void addUserShouldValidateUserLoginWithSpaces() {
        User user = new User(1, "Test@yandex.ru", "Test Test", "Test", LocalDate.of(12,12,20));

        Exception exception = assertThrows(ValidationException.class, () -> UserValidator.validate(user));
        assertEquals("Login cannot be empty and contain spaces", exception.getMessage());
    }

    @Test
    void addUserShouldValidateUserWithEmptyName() {
        User user = new User(1, "Test@yandex.ru", "Averin", "", LocalDate.of(12,12,20));
        UserValidator.checkName(user);
        assertEquals("Averin", user.getName());
    }
}
