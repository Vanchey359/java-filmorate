package ru.yandex.practicum.filmorate.storage.like;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.List;

@Component
@Slf4j
public class LikesStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;

    public LikesStorage(JdbcTemplate jdbcTemplate,
                        GenreStorage genreStorage,
                        MpaStorage mpaStorage,
                        FilmDbStorage filmDbStorage, UserDbStorage userDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
        this.filmDbStorage = filmDbStorage;
        this.userDbStorage = userDbStorage;
    }

    public void addLike(Long id, Long userId) {
        if (!filmDbStorage.isFilmExists(id)) {
            throw new NotFoundException("Film not found");
        }
        if (!userDbStorage.isUserExists(userId)) {
            throw new NotFoundException("User not found");
        }
        String sql = "INSERT INTO likes (user_id, film_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, id);
        log.info("User id = {} add like to film id = {}", userId, id);
    }

    public void removeLike(Long id, Long userId) {
        if (!filmDbStorage.isFilmExists(id)) {
            throw new NotFoundException("Film not found");
        }
        if (!userDbStorage.isUserExists(userId)) {
            throw new NotFoundException("User not found");
        }
        if (!isLikeExist(userId, id)) {
            throw new NotFoundException("User didn't add like to film");
        }
        String sql = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";
        jdbcTemplate.update(sql, userId, id);
    }

    private boolean isLikeExist(Long userId, Long filmId) {
        String sql = "SELECT * FROM likes WHERE user_id = ? AND film_id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sql, userId, filmId);
        return userRows.next();
    }

    public List<Film> getPopular(int count) {
        String sql = "SELECT films.film_id, name, description, release_date, duration, mpa_id, " +
                "COUNT(L.user_id) AS RAIT FROM films LEFT JOIN likes L on films.film_id = L.film_id " +
                "GROUP BY films.film_id " +
                "ORDER BY RAIT DESC LIMIT ?";
        System.out.println(count);
        List <Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> new Film(
                rs.getLong("film_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getInt("duration"),
                genreStorage.getFilmGenres(rs.getLong("film_id")),
                mpaStorage.getMpa(rs.getInt("mpa_id")),
                rs.getLong("RAIT")
        ), count);
        return films;
    }
}