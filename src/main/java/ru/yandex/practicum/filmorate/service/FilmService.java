package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final FilmValidator filmValidator;

    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film getById(long id) {
        return filmStorage.getById(id);
    }

    public Film add(Film film) {
        filmValidator.validate(film);
        return filmStorage.add(film);
    }

    public Film update(Film film) {
        filmValidator.validate(film);
        return filmStorage.update(film);
    }

    public void addLike(long id, long userId) {
        Film film = filmStorage.getById(id);
        film.addLike(userId);
        filmStorage.update(film);
        log.info("Film with id = {} was liked by user with id = {}", film.getId(), userId);
    }

    public void removeLike(long id, long userId) {
        Film film = filmStorage.getById(id);
        boolean result = film.removeLike(userId);
        if (!result) {
            throw new NotFoundException("Like not found, nothing to delete!");
        }
        filmStorage.update(film);
        log.info("User with id = {} was deleted like from movie id = {}", userId, film.getId());
    }

    public List<Film> getTop(int count) {
        if (count <= 0) {
            throw new NotFoundException("The number of films cannot be negative or equal to zero!");
        }
        return filmStorage.getAll().stream()
                .sorted(Comparator.comparingInt(Film::countLikes).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}
