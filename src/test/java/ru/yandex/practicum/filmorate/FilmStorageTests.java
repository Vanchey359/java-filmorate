package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmStorageTests {

    private final FilmDbStorage filmDbStorage;

    @Test
    public void getByIdShouldGetFilmById() {
        Film film = new Film(null, "name", "description1",
                LocalDate.of(1975, 5, 17),
                100);
        film.setMpa(new Mpa(1, null));
        filmDbStorage.add(film);
        Long id = film.getId();
        Optional<Film> filmOptional = filmDbStorage.getById(id);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(f ->
                        assertThat(f).hasFieldOrPropertyWithValue("id", id)
                );
        filmDbStorage.delete(film);
    }

    @Test
    public void updateShouldUpdateFilmFromDb() {
        Film film = new Film(null, "name", "description2",
                LocalDate.of(1975, 5, 17),
                100);
        film.setMpa(new Mpa(1, null));
        Film filmUpdate = new Film(null, "NewName", "description22",
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
    public void deleteShouldDeleteFilmFromDb() {
        Film film = new Film(null, "name", "description3",
                LocalDate.of(1975, 5, 17),
                100);
        film.setMpa(new Mpa(1, null));
        filmDbStorage.add(film);
        filmDbStorage.delete(film);
        Optional<Film> filmOptional = filmDbStorage.getById(1L);
        assertThat(filmOptional).isEmpty();
    }
}
