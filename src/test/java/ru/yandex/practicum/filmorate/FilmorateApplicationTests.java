package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class FilmorateApplicationTests {

    static FilmController filmController = new FilmController();
    static UserController userController = new UserController();

    @Test
    void shouldValidateFilmWithOldReleaseDate() {
        Film film = new Film();
        film.setName("Test");
        film.setDescription("TestTest");
        film.setDuration(45);
        film.setReleaseDate(LocalDate.of(1300, 11, 12));
        Exception exception = assertThrows(ValidationException.class, () -> filmController.addFilm(film));
        assertEquals("Release date must not be earlier than 28.12.1895", exception.getMessage());
    }

    @Test
    void shouldValidateUserLoginWithSpaces() {
        User user = new User();
        user.setBirthday(LocalDate.of(2000,12,12));
        user.setLogin("Ivan Averin");

        Exception exception = assertThrows(ValidationException.class, () -> userController.addUser(user));
        assertEquals("Login cannot be empty and contain spaces", exception.getMessage());
    }

    @Test
    void shouldValidateUserWithEmptyName() {
        User user = new User();
        user.setEmail("yandex@yandex.ru");
        user.setLogin("Averin");
        user.setBirthday(LocalDate.of(2012, 12, 12));
        userController.addUser(user);
        assertEquals("Averin", user.getName());
    }
}
