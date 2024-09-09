package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final FilmStorage filmStorage;
    private final FilmService filmService;
    private final UserStorage userStorage;

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Пришел GET запрос /films");
        Collection<Film> allFilms = filmStorage.findAll();
        log.info("Отправлен ответ GET /films с телом: {}", allFilms);
        return allFilms;
    }

    @GetMapping("/{filmId}")
    public Film findById(@PathVariable Long filmId) {
        log.info("Пришел GET запрос /films/{}", filmId);
        final Film film = filmStorage.findById(filmId);
        if (film == null) {
            log.info("Запрос GET /films/{} обработан не был по причине: Фильм с id = {} не найден", filmId, filmId);
            throw new NotFoundException("Фильм с id = " + filmId + " не найден.");
        }
        log.info("Отправлен ответ GET /films/{} с телом: {}", filmId, film);
        return film;
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

    @PutMapping("/{filmId}/like/{userId}")
    public Film addLike(@PathVariable Long filmId, @PathVariable Long userId) {
        log.info("Пришел PUT запрос /films/{}/like/{}", filmId, userId);
        final Film film = filmStorage.findById(filmId);
        final User user = userStorage.findById(userId);
        if (film == null) {
            log.info("Запрос PUT /films/{}/like/{} обработан не был по причине: Фильм с id = {} не найден", filmId, userId, filmId);
            throw new NotFoundException("Фильм с id = " + filmId + " не найден.");
        }
        if (user == null) {
            log.info("Запрос PUT /films/{}/like/{} обработан не был по причине: Пользователь с id = {} не найден", filmId, userId, userId);
            throw new NotFoundException("Пользователь с id = " + userId + " не найден.");
        }
        filmService.likeFilm(film, user);
        log.info("Отправлен ответ PUT /films/{}/like/{} с телом: {}", filmId, userId, film);
        return film;
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public Film deleteLike(@PathVariable Long filmId, @PathVariable Long userId) {
        log.info("Пришел DELETE запрос /films/{}/like/{}", filmId, userId);
        final Film film = filmStorage.findById(filmId);
        final User user = userStorage.findById(userId);
        if (film == null) {
            log.info("Запрос DELETE /films/{}/like/{} обработан не был по причине: Фильм с id = {} не найден", filmId, userId, filmId);
            throw new NotFoundException("Фильм с id = " + filmId + " не найден.");
        }
        if (user == null) {
            log.info("Запрос DELETE /films/{}/like/{} обработан не был по причине: Пользователь с id = {} не найден", filmId, userId, userId);
            throw new NotFoundException("Пользователь с id = " + userId + " не найден.");
        }
        filmService.unlikeFilm(film, user);
        log.info("Отправлен ответ DELETE /films/{}/like/{} с телом: {}", filmId, userId, film);
        return film;
    }

    @GetMapping("/popular")
    public Collection<Film> getPopular(@RequestParam(required = false) Long count) {
        log.info("Пришел GET запрос /popular/count={}", count);
        Collection<Film> topFilms = filmService.getTopFilmsByLike(count);
        log.info("Отправлен ответ GET /popular/count={} с телом: {}", count, topFilms);
        return topFilms;
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
