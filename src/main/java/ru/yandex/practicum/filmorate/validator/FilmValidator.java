package ru.yandex.practicum.filmorate.validator;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@Component
public class FilmValidator {

    private static final LocalDate DAY_OF_FILMS_BIRTHDAY = LocalDate.of(1895, 12, 28);

    public void validate(Film film) {
        if (film.getReleaseDate().isBefore(DAY_OF_FILMS_BIRTHDAY)) {
            throw new ValidationException("Release date must not be earlier than 28.12.1895");
        }
    }
}
