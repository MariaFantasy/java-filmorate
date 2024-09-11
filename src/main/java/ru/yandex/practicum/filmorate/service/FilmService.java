package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    private static final long TOP_LIMIT_N = 10;

    private final FilmStorage filmStorage;
    private final UserService userService;

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film findById(Long filmId) {
        return filmStorage.findById(filmId);
    }

    public void create(Film film) {
        filmStorage.create(film);
    }

    public void update(Film film) {
        filmStorage.update(film);
    }

    public Film likeFilm(Long filmId, Long userId) {
        final Film film = filmStorage.findById(filmId);
        final User user = userService.findById(userId);
        if (film == null) {
            throw new NotFoundException("Фильм с id = " + filmId + " не найден.");
        }
        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден.");
        }
        film.getLikedUsers().add(user.getId());
        return film;
    }

    public Film unlikeFilm(Long filmId, Long userId) {
        final Film film = filmStorage.findById(filmId);
        final User user = userService.findById(userId);
        if (film == null) {
            throw new NotFoundException("Фильм с id = " + filmId + " не найден.");
        }
        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден.");
        }
        film.getLikedUsers().remove(user.getId());
        return film;
    }

    public List<Film> getTopFilmsByLike(Long count) {
        if (count == null) {
            count = TOP_LIMIT_N;
        }
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparingLong(film -> -film.getLikedUsers().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}
