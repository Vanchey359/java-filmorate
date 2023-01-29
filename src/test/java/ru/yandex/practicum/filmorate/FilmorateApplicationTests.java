package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
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
	void shouldValidateFilmWithEmptyNameAndEmptyAllFields() {
		Film film = new Film();
		Exception exception = assertThrows(ValidationException.class, () -> filmController.validate(film));
		assertEquals("Film title cannot be empty", exception.getMessage());

		film.setName(" ");
		exception = assertThrows(ValidationException.class, () -> filmController.validate(film));
		assertEquals("Film title cannot be empty", exception.getMessage());
	}

	@Test
	void shouldValidateFilmWithBigDescription() {
		Film film = new Film();
		film.setName("Test");
		film.setDescription("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff" +
				"fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff");
		Exception exception = assertThrows(ValidationException.class, () -> filmController.validate(film));
		assertEquals("The maximum length of a film description is 200 characters.", exception.getMessage());
	}

	@Test
	void shouldValidateFilmWithOldReleaseDate() {
		Film film = new Film();
		film.setName("Test");
		film.setDescription("TestTest");
		film.setDuration(45);
		film.setReleaseDate(LocalDate.of(1300,11,12));
		Exception exception = assertThrows(ValidationException.class, () -> filmController.validate(film));
		assertEquals("Release date must not be earlier than 28.12.1895", exception.getMessage());
	}

	@Test
	void shouldValidateFilmWithNegativeDuration() {
		Film film = new Film();
		film.setName("Test");
		film.setDescription("TestTest");
		film.setReleaseDate(LocalDate.of(2012,12,12));
		film.setDuration(-4);
		Exception exception = assertThrows(ValidationException.class, () -> filmController.validate(film));
		assertEquals("Film duration must be positive", exception.getMessage());
	}

	@Test
	void shouldValidateEmptyUserEmailAndEmptyAllFields() {
		User user = new User();
		Exception exception = assertThrows(ValidationException.class, () -> userController.validate(user));
		assertEquals("Email cannot be empty and must contain the @ symbol", exception.getMessage());

		user.setEmail("ivan.yandex.ru");
		exception = assertThrows(ValidationException.class, () -> userController.validate(user));
		assertEquals("Email cannot be empty and must contain the @ symbol", exception.getMessage());
	}

	@Test
	void shouldValidateEmptyUserLogin() {
		User user = new User();
		user.setEmail("yandex@yandex.ru");
		Exception exception = assertThrows(ValidationException.class, () -> userController.validate(user));
		assertEquals("Login cannot be empty and contain spaces", exception.getMessage());

		user.setLogin("Ivan Averin");
		exception = assertThrows(ValidationException.class, () -> userController.validate(user));
		assertEquals("Login cannot be empty and contain spaces", exception.getMessage());
	}

	@Test
	void shouldValidateUserWithEmptyName() {
		User user = new User();
		user.setEmail("yandex@yandex.ru");
		user.setLogin("Averin");
		user.setBirthday(LocalDate.of(2012,12,12));
		userController.validate(user);
		assertEquals("Averin", user.getName());
	}

	@Test
	void shouldValidateUserWasBornInFuture() {
		User user = new User();
		user.setName("Ivan");
		user.setLogin("Averin");
		user.setEmail("yandex@yandex.ru");
		user.setBirthday(LocalDate.of(3000,10,10));
		Exception exception = assertThrows(ValidationException.class, () -> userController.validate(user));
		assertEquals("Date of birth cannot be in the future", exception.getMessage());

	}
}
