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

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.debug("Film has not been created: Name is empty");
            throw new ConditionsNotMetException("Название не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.debug("Film has not been created: Description too long");
            throw new ConditionsNotMetException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.debug("Film has not been created: Release Date is too early");
            throw new ConditionsNotMetException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        if (!film.getDuration().isPositive()) {
            log.debug("Film has not been created: Duration is not positive");
            throw new ConditionsNotMetException("Продолжительность фильма должна быть положительным числом");
        }
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Film with id " + film.getId()  + " created");
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        if (newFilm.getId() == null) {
            log.debug("Film has not been updated: Id is empty");
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            if (newFilm.getName() == null || newFilm.getName().isBlank()) {
                log.debug("Film has not been updated: Name is empty");
                throw new ConditionsNotMetException("Название не может быть пустым");
            }
            if (newFilm.getDescription().length() > 200) {
                log.debug("Film has not been updated: Description too long");
                throw new ConditionsNotMetException("Максимальная длина описания — 200 символов");
            }
            if (newFilm.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
                log.debug("Film has not been updated: Release Date is too early");
                throw new ConditionsNotMetException("Дата релиза — не раньше 28 декабря 1895 года");
            }
            if (!newFilm.getDuration().isPositive()) {
                log.debug("Film has not been updated: Duration is not positive");
                throw new ConditionsNotMetException("Продолжительность фильма должна быть положительным числом");
            }
            oldFilm.setName(newFilm.getName());
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            oldFilm.setDuration(newFilm.getDuration());
            log.info("Film with id " + oldFilm.getId()  + " updated");
            return oldFilm;
        }
        log.info("Film has not been updated: Id not found");
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
