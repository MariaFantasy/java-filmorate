package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.Collection;

@RestController
@RequestMapping("/films")
public class FilmController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private FilmStorage filmStorage = new InMemoryFilmStorage();

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Пришел GET запрос /films");
        Collection<Film> allFilms = filmStorage.findAll();
        log.info("Отправлен ответ GET /films с телом: {}", allFilms);
        return allFilms;
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Пришел POST запрос /films с телом: {}", film);
        validate(film);
        filmStorage.create(film);
        log.info("Отправлен ответ POST /films с телом: {}", film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Пришел PUT запрос /films с телом: {}", film);
        final Long filmId = film.getId();
        if (filmId == null) {
            log.info("Запрос PUT /films обработан не был по причине: Id должен быть указан");
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (filmStorage.findById(filmId) != null) {
            validate(film);
            filmStorage.update(film);
            log.info("Отправлен ответ PUT /films с телом: {}", film);
            return film;
        }
        log.info("Запрос PUT /films обработан не был по причине: Фильм с id = {} не найден", filmId);
        throw new NotFoundException("Фильм с id = " + filmId + " не найден");
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
