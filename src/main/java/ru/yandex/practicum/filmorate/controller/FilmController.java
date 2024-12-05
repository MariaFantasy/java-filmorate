package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.Collection;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final FilmService filmService;

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Пришел GET запрос /films");
        Collection<Film> allFilms = filmService.findAll();
        log.info("Отправлен ответ GET /films с телом: {}", allFilms);
        return allFilms;
    }

    @GetMapping("/{filmId}")
    public Film findById(@PathVariable Long filmId) {
        log.info("Пришел GET запрос /films/{}", filmId);
        final Film film = filmService.findById(filmId);
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
        Film createdFilm = filmService.create(film);
        log.info("Отправлен ответ POST /films с телом: {}", createdFilm);
        return createdFilm;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Пришел PUT запрос /films с телом: {}", film);
        final Long filmId = film.getId();
        if (filmId == null) {
            log.info("Запрос PUT /films обработан не был по причине: Id должен быть указан");
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (filmService.findById(filmId) != null) {
            validate(film);
            Film updatedFilm = filmService.update(film);
            log.info("Отправлен ответ PUT /films с телом: {}", updatedFilm);
            return updatedFilm;
        }
        log.info("Запрос PUT /films обработан не был по причине: Фильм с id = {} не найден", filmId);
        throw new NotFoundException("Фильм с id = " + filmId + " не найден");
    }

    @PutMapping("/{filmId}/like/{userId}")
    public Film addLike(@PathVariable Long filmId, @PathVariable Long userId) {
        log.info("Пришел PUT запрос /films/{}/like/{}", filmId, userId);
        Film film = filmService.likeFilm(filmId, userId);
        log.info("Отправлен ответ PUT /films/{}/like/{} с телом: {}", filmId, userId, film);
        return film;
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public Film deleteLike(@PathVariable Long filmId, @PathVariable Long userId) {
        log.info("Пришел DELETE запрос /films/{}/like/{}", filmId, userId);
        Film film = filmService.unlikeFilm(filmId, userId);
        log.info("Отправлен ответ DELETE /films/{}/like/{} с телом: {}", filmId, userId, film);
        return film;
    }

    @GetMapping("/popular")
    public Collection<Film> getPopular(@RequestParam(defaultValue = "10") Long count,
                                       @RequestParam(required = false) Integer genreId,
                                       @RequestParam(required = false) Integer year) {
        log.info("Пришел GET запрос /popular?count={}&genreId={}&year={}", count, genreId, year);
        Collection<Film> topFilms = filmService.getTopFilmsByLike(count, genreId, year);
        log.info("Отправлен ответ GET /popular?count={}&genreId={}&year={} с телом: {}", count, genreId, year, topFilms);
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
