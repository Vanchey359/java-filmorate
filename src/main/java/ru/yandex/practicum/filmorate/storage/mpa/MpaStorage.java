package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

@Component
public class MpaStorage {
    private final JdbcTemplate jdbcTemplate;
    public MpaStorage (JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Mpa getMpa(int mpaId) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT mpa_name FROM rating_mpa WHERE mpa_id = ?", mpaId);

        if (userRows.next()) {
            return new Mpa(mpaId,
                    userRows.getString("mpa_name"));
        } else {
            return null;
        }
    }
}
