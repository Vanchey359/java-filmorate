package ru.yandex.practicum.filmorate.controller;

import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();
    private int idCounter = 0;
    private static final LocalDate DAY_OF_FILMS_BIRTHDAY = LocalDate.of(1895, 12, 28);

    @GetMapping
    public List<Film> findAll() {
        log.info("Got a list of all films");
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        validate(film);
        film.setId(++idCounter);
        films.put(film.getId(), film);
        log.info("Film {} was added to the list of all films", film.getName());
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        validate(film);
        if (!films.containsKey(film.getId())) {
            throw new ValidationException("Film with this id was not found");
        }
        films.put(film.getId(), film);
        log.info("Film {} has been updated", film.getName());
        return film;
    }

    private void validate(Film film) {
        if (film.getReleaseDate().isBefore(DAY_OF_FILMS_BIRTHDAY)) {
            throw new ValidationException("Release date must not be earlier than 28.12.1895");
        }
    }
}
