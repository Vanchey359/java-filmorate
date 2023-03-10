package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Set;
import java.util.TreeSet;

@Component
public class GenreStorage {
    private final JdbcTemplate jdbcTemplate;
    public GenreStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Set<Genre> getFilmGenres(Long filmId) {
        String sql = "SELECT genres.genre_id, genre FROM film_genre JOIN genres " +
                "ON film_genre.genre_id = genres.genre_id " +
                "WHERE film_id = ?";
        return new TreeSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> new Genre(
                        rs.getInt("genre_id"),
                        rs.getString("genre")),
                filmId
        ));
    }

    public void updateGenresOfFilm(Film film) {
        jdbcTemplate.update("DELETE FROM film_genre WHERE film_id = ?", film.getId());
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update("INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)",
                        film.getId(), genre.getId());
            }
        }
    }
}