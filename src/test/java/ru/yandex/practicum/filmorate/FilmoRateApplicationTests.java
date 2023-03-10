package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmoRateApplicationTests {
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;

    @Test
    public void testFindUserById() {
        User user1 = new User(null, "ivanaverin@yandex.ru", "logIvan", "Ivan", LocalDate.of(1946, 8, 20));
        userDbStorage.add(user1);
        long id = user1.getId();

        Optional<User> userOptional = userDbStorage.getById(id);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", id)
                );
    }

    @Test
    public void testUserUpdate() {
        User user1 = new User(null, "ivanaverin@yandex.ru", "logIvan", "Ivan", LocalDate.of(1946, 8, 20));
        userDbStorage.add(user1);
        long id = user1.getId();

        User userUpdate = new User(null, "ivanaverin@yandex.ru", "logUpdate", "Ivan", LocalDate.of(1946, 8, 20));
        userUpdate.setId(id);
        userDbStorage.update(userUpdate);

        Optional<User> userOptional = userDbStorage.getById(id);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(u -> assertThat(u)
                        .hasFieldOrPropertyWithValue("login", "logUpdate"));
    }

    @Test
    public void testUserDelete() {
        User user1 = new User(null, "ivanaverin@yandex.ru", "logIvan", "Ivan", LocalDate.of(1946, 8, 20));
        userDbStorage.add(user1);
        userDbStorage.delete(user1);

        Optional<User> userOptional = userDbStorage.getById(1L);
        assertThat(userOptional).isEmpty();
    }

    @Test
    public void testFindFilmById() {
        Film film = new Film(null, "name", "description",
                LocalDate.of(1975, 5, 17),
                100);
        film.setMpa(new Mpa(1, null));
        filmDbStorage.add(film);
        Long id = film.getId();
        Optional<Film> filmOptional =filmDbStorage.getById(id);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(f ->
                        assertThat(f).hasFieldOrPropertyWithValue("id", id)
                );
    }

    @Test
    public void testFilmUpdate() {
        Film film = new Film(null, "name", "description",
                LocalDate.of(1975, 5, 17),
                100);
        film.setMpa(new Mpa(1, null));
        Film filmUpdate = new Film(null, "NewName", "description",
                LocalDate.of(1975, 5, 17),
                100);
        filmUpdate.setMpa(new Mpa(1, null));
        filmDbStorage.add(film);
        Long id = film.getId();
        filmUpdate.setId(id);
        filmDbStorage.update(filmUpdate);
        Optional<Film> filmOptional = filmDbStorage.getById(id);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(u -> assertThat(u)
                        .hasFieldOrPropertyWithValue("name", "NewName"));
    }

    @Test
    public void testFilmDelete() {
        Film film = new Film(null, "name", "description",
                LocalDate.of(1975, 5, 17),
                100);
        film.setMpa(new Mpa(1, null));
        filmDbStorage.add(film);
        filmDbStorage.delete(film);
        Optional<Film> filmOptional = filmDbStorage.getById(1L);
        assertThat(filmOptional).isEmpty();
    }
}