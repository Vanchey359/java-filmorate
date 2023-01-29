package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
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
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final List<Film> films = new ArrayList<>();
    private int idCounter = 0;
    private static final LocalDate DAY_OF_FILMS_BIRTHDAY = LocalDate.of(1895,12,28);

    @GetMapping
    public List<Film> findAll() {
        log.info("Получен список всех фильмов");
        return films;
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        validate(film);
        film.setId(++idCounter);
        films.add(film);
        log.info("Фильм {} был добавлен в список всех фильмов", film.getName());
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        validate(film);
        for (Film fl : films) {
            if (fl.getId() == film.getId()) {
                films.remove(fl);
                films.add(film);
                log.info("Фильм {} был обновлен", film.getName());
            } else {
                log.warn("Фильм с таким id не найден");
                throw new ValidationException("Film with this id was not found");
            }
        }
        return film;
    }

    public void validate(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Запрос на действие с фильмом без имени");
            throw new ValidationException("Film title cannot be empty");
        }

        if (film.getDescription().length() > 200) {
            log.warn("Запрос на действие с фильмом длинна описания которого больше 200 символов");       // Не особо понял зачем я это пишу, ведь когда программа выбросит исключение, там будет сообщение с причиной ошибки
            throw new ValidationException("The maximum length of a film description is 200 characters.");
        }

        if (film.getReleaseDate().isBefore(DAY_OF_FILMS_BIRTHDAY)) {
            log.warn("Запрос на действие с фильмом дата релиза которого раньше чем день рождения кино");
            throw new ValidationException("Release date must not be earlier than 28.12.1895");
        }

        if (film.getDuration() <= 0) {
            log.warn("Запрос на действие с фильмом длительность которого меньше 0");
            throw new ValidationException("Film duration must be positive");
        }
    }
}
