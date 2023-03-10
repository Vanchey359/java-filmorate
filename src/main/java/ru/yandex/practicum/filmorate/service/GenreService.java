package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

@Component
@Slf4j
public class GenreService {

    private final JdbcTemplate jdbcTemplate;

    public GenreService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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
