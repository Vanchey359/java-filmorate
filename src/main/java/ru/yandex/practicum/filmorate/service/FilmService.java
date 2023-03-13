package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.like.LikesStorage;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final FilmValidator filmValidator;
    private final LikesStorage likesStorage;
    private Long id = 0L;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, FilmValidator filmValidator, LikesStorage likesStorage) {
        this.filmStorage = filmStorage;
        this.filmValidator = filmValidator;
        this.likesStorage = likesStorage;
    }

    private Long generateId() {
        return ++id;
    }

    public Film add(Film film) {
        filmValidator.validate(film);
        return filmStorage.add(film);
    }

    public Film update(Film film) {
        filmValidator.validate(film);
        filmStorage.update(film);
        Film filmReturn = filmStorage.getById(film.getId()).orElseThrow(
                () -> new NotFoundException("Request film with absent id"));
        if (film.getGenres() == null) {
            filmReturn.setGenres(null);
        } else if (film.getGenres().isEmpty()) {
            filmReturn.setGenres(new HashSet<>());
        }
        return filmReturn;
    }

    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film getById(Long id) {
        return filmStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Request film with absent id"));
    }

    public Film delete(Film film) {
        filmStorage.delete(film);
        return film;
    }

    public void addLike(Long id, Long userId) {
        likesStorage.addLike(id, userId);
        log.info("User id = {} set like film id = {}", userId, id);
    }

    public void removeLike(Long id, Long userId) {
        likesStorage.removeLike(id, userId);
        log.info("User id = {} deleted like to film id = {}", userId, id);
    }

    public List<Film> getFilmsByRating(int count) {
        return likesStorage.getPopular(count);
    }
}