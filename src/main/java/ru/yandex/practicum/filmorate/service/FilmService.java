package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {

    private final static int TOP = 10;
    private final FilmStorage filmStorage;

    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film getById(long id) {
        return filmStorage.getById(id);
    }

    public Film add(Film film) {
        FilmValidator.validate(film);
        return filmStorage.add(film);
    }

    public Film update(Film film) {
        FilmValidator.validate(film);
        return filmStorage.update(film);
    }

    public Film addLike(long id, long userId) throws NotFoundException {
        Film film = filmStorage.getById(id);
        film.addLike(userId);
        filmStorage.update(film);
        log.info("Film with id = {} was liked by user with id = {}", film.getId(), userId);
        return film;
    }

    public Film removeLike(long id, long userId) throws NotFoundException {
        Film film = filmStorage.getById(id);
        boolean result = film.removeLike(userId);
        if (!result) {
            throw new NotFoundException("Like not found, nothing to delete!");
        }
        filmStorage.update(film);
        log.info("User with id = {} was deleted like from movie id = {}", userId, film.getId());
        return film;
    }

    public List<Film> getTop(int count) {
        if (count == 0) {
            count = TOP;
        }
        List<Film> films = filmStorage.getAll();
        films.sort(Comparator.comparingInt(Film::countLikes).reversed());
        return films.stream()
                .limit(count)
                .collect(Collectors.toList());
    }
}
