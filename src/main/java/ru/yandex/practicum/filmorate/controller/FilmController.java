package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
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

    @PutMapping("/{filmId}/like/{userId}/{mark}")
    public Film addLike(@PathVariable Long filmId, @PathVariable Long userId, @PathVariable double mark) {
        log.info("Пришел PUT запрос /films/{}/like/{}/{}", filmId, userId, mark);
        Film film = filmService.likeFilm(filmId, userId, mark);
        log.info("Отправлен ответ PUT /films/{}/like/{}/{} с телом: {}", filmId, userId, mark, film);
        return film;
    }

    @PutMapping("/{filmId}/like/{userId}")
    public Film addLike(@PathVariable Long filmId, @PathVariable Long userId) {
        log.info("Пришел PUT запрос /films/{}/like/{} Ставлю дефолтную оценку 6", filmId, userId);
        Film film = filmService.likeFilm(filmId, userId, 6D);
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

    @GetMapping("/common")
    public Collection<Film> getCommon(@RequestParam Long userId, @RequestParam Long friendId) {
        log.info("Пришел GET запрос /common?userId={}&friendId={}", userId, friendId);
        Collection<Film> commonFilms = filmService.getCommonUserFilms(userId, friendId);
        log.info("Отправлен ответ GET /common?userId={}&friendId={} с телом: {}", userId, friendId, commonFilms);
        return commonFilms;
    }

    //TODO Мария: Добавить сортировку по директору /films/director/1?sortBy=rate
    @GetMapping("/director/{directorId}")
    public Collection<Film> getFilmsByDirector(@PathVariable Long directorId, @RequestParam String sortBy) {
        log.info("Пришел GET запрос /films/director/{}?sortBy={}", directorId, sortBy);
        if (sortBy != null && !sortBy.equals("year") && !sortBy.equals("likes")) {
            throw new ConditionsNotMetException("Сортировки " + sortBy + " пока не существует.");
        }
        Collection<Film> films = filmService.getByDirector(directorId, sortBy);
        log.info("Отправлен ответ GET /films/director/{}?sortBy={} с телом: {}", directorId, sortBy, films);
        return films;
    }

    @DeleteMapping("/{filmId}")
    public Film delete(@PathVariable Long filmId) {
        log.info("Пришел DELETE запрос /films/{}", filmId);
        final Film film = filmService.findById(filmId);
        filmService.delete(film);
        log.info("Отправлен ответ DELETE /films/{} с телом: {}", filmId, film);
        return film;
    }

    @GetMapping("/search")
    public Collection<Film> search(@RequestParam String query, @RequestParam String by) {
        log.info("Пришел GET запрос /films/search с query={} и by={}", query, by);
        Collection<Film> filmsFound = filmService.search(query, by);
        log.info("Отправлен ответ GET /films/search с телом: {}", filmsFound);
        return filmsFound;
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
