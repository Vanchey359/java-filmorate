package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

@Component
@Slf4j
public class MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    public MpaStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Collection<Mpa> getAllMpa() {
        return jdbcTemplate.query("SELECT * FROM rating_mpa",
                ((rs, rowNum) -> new Mpa(
                        rs.getInt("mpa_id"),
                        rs.getString("mpa_name"))
                ));
    }

    public Mpa getMpa(int id) {
        SqlRowSet userRows =
                jdbcTemplate.queryForRowSet("SELECT mpa_name FROM rating_mpa WHERE mpa_id = ?", id);
        if (userRows.next()) {
            Mpa mpa = new Mpa(
                    id,
                    userRows.getString("mpa_name")
            );
            log.info("Response mpa = {} ", mpa);
            return mpa;
        } else throw new NotFoundException("Attempt to get mpa with absent id");
    }
}
