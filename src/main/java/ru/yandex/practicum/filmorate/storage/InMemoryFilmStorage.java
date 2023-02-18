package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private long currentId = 0L;
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public List<Film> getAll() {
        log.info("Got a list of all films");
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getById(long id) throws NotFoundException {
        Film film = films.get(id);
        if (film == null) {
            throw new NotFoundException("Film with id was not found");
        }
        log.info("Got a film with id = {}", film.getId());
        return film;
    }

    @Override
    public Film add(Film film) {
        film.setId(++currentId);
        films.put(currentId, film);
        log.info("Film {} was added to the list of all films", film.getName());
        return film;
    }

    @Override
    public Film update(Film film) throws NotFoundException {
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Film with this id was not found");
        }
        films.put(film.getId(), film);
        log.info("Film {} has been updated", film.getName());
        return film;
    }

    @Override
    public void delete(Film film) {
        films.remove(film.getId());
        log.info("Film {} has been deleted", film.getName());
    }
}
