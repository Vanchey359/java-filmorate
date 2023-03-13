package ru.yandex.practicum.filmorate.storage.genre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

@Component
@Slf4j
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

    public Collection<Genre> getGenres() {
        return jdbcTemplate.query("SELECT * FROM genres",
                ((rs, rowNum) -> new Genre(
                        rs.getInt("genre_id"),
                        rs.getString("genre"))
                ));
    }


    public Genre getGenre(int id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT genre FROM genres WHERE genre_id = ?", id);
        if (userRows.next()) {
            Genre genre = new Genre(
                    id,
                    userRows.getString("genre")
            );
            log.info("Response genre = {} ", genre);
            return genre;
        } else {
            throw new NotFoundException("Attempt to get genre with absent id");
        }
    }
}