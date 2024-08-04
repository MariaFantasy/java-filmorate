package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final Map<Long, Film> films = new HashMap<>();
    private long filmCounter = 0;

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        validateFilm(film);
        final long filmId = getNextId();
        film.setId(filmId);
        films.put(filmId, film);
        log.info("Film with id " + filmId  + " created");
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        final Long filmId = film.getId();
        if (filmId == null) {
            log.debug("Film has not been updated: Id is empty");
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (films.containsKey(filmId)) {
            validateFilm(film);
            films.put(filmId, film);
            log.info("Film with id " + filmId  + " updated");
            return film;
        }
        log.info("Film has not been updated: Id not found");
        throw new NotFoundException("Фильм с id = " + filmId + " не найден");
    }

    private long getNextId() {
        return ++filmCounter;
    }

    private void validateFilm(final Film film) {
        if (film.getDescription().length() > 200) {
            log.debug("Film has not been created: Description too long");
            throw new ConditionsNotMetException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.debug("Film has not been created: Release Date is too early");
            throw new ConditionsNotMetException("Дата релиза — не раньше 28 декабря 1895 года");
        }
    }
}
