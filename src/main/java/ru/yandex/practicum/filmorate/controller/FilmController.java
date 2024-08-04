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
        log.info("Пришел GET запрос /films");
        log.info("Отправлен ответ GET /films с телом: " + films.values());
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Пришел POST запрос /films с телом: " + film);
        validate(film);
        final long filmId = getNextId();
        film.setId(filmId);
        films.put(filmId, film);
        log.info("Отправлен ответ POST /films с телом: " + film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Пришел PUT запрос /films с телом: " + film);
        final Long filmId = film.getId();
        if (filmId == null) {
            log.info("Запрос PUT /films обработан не был по причине: Id должен быть указан");
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (films.containsKey(filmId)) {
            validate(film);
            films.put(filmId, film);
            log.info("Отправлен ответ PUT /films с телом: " + film);
            return film;
        }
        log.info("Запрос PUT /films обработан не был по причине: Фильм с id = " + filmId + " не найден");
        throw new NotFoundException("Фильм с id = " + filmId + " не найден");
    }

    private long getNextId() {
        return ++filmCounter;
    }

    private void validate(final Film film) {
        if (film.getDescription().length() > 200) {
            log.debug("Фильм не прошел валидацию по причине: Максимальная длина описания — 200 символов");
            throw new ConditionsNotMetException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.debug("Фильм не прошел валидацию по причине: Дата релиза — не раньше 28 декабря 1895 года");
            throw new ConditionsNotMetException("Дата релиза — не раньше 28 декабря 1895 года");
        }
    }
}
