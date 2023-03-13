package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Component
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreStorage genreStorage, MpaStorage mpaStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
    }

    @Override
    public Collection<Film> getAll() {
        String sql = "SELECT * FROM films ";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Film(
                rs.getLong("film_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getInt("duration"),
                genreStorage.getFilmGenres(rs.getLong("film_id")),
                mpaStorage.getMpa(rs.getInt("MPA_id"))
        ));
    }

    @Override
    public Optional<Film> getById(Long id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM films WHERE film_id = ?", id);
        if (userRows.next()) {
            Film film = new Film(
                    userRows.getLong("film_id"),
                    userRows.getString("name"),
                    userRows.getString("description"),
                    userRows.getDate("release_date").toLocalDate(),
                    userRows.getInt("duration")
            );
            Integer rateMpa = userRows.getInt("MPA_id");
            film.setMpa(mpaStorage.getMpa(rateMpa));
            Set<Genre> genres = genreStorage.getFilmGenres(id);
            film.setGenres(genreStorage.getFilmGenres(id));
            log.info("Found film id = {}", film);
            return Optional.of(film);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Film add(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FILMS")
                .usingGeneratedKeyColumns("film_id");
        film.setId(simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue());
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update("INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)",
                        film.getId(), genre.getId());
            }
        }
        jdbcTemplate.update("UPDATE films SET name=?, description=?, release_date=?, duration=?, mpa_id=? WHERE film_id=?", film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId(), film.getId());

        log.info("New film added: {}", film);
        return film;
    }

    @Override
    public Film update(Film film) {
        if (!isFilmExists(film.getId())) {
            throw new NotFoundException("Attempt to update non-existent film");
        }
        jdbcTemplate.update("UPDATE films SET name=?, description=?, release_date=?, duration=?, mpa_id=? WHERE film_id=?", film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId(), film.getId());
        genreStorage.updateGenresOfFilm(film);
        log.info("Film {} has been successfully updated", film);
        return film;
    }

    @Override
    public Film delete(Film film) {
        if (!isFilmExists(film.getId())) {
            throw new NotFoundException("Attempt to delete non-existent film");
        }
        String sql = "DELETE FROM films WHERE film_id = ?";
        jdbcTemplate.update(sql, film.getId());
        return film;
    }

    public boolean isFilmExists(Long id) {
        String sql = "SELECT * FROM films WHERE film_id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sql, id);
        return userRows.next();
    }
}
