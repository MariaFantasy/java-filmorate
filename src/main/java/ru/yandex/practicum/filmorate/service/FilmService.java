package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private static final long TOP_LIMIT_N = 10;

    private FilmStorage filmStorage;

    public void likeFilm(Film film, User user) {
        film.getLikedUsers().add(user.getId());
        user.getLikedFilms().add(film.getId());
    }

    public void unlikeFilm(Film film, User user) {
        film.getLikedUsers().remove(user.getId());
        user.getLikedFilms().remove(film.getId());
    }

    public List<Film> getTopFilmsByLike() {
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparingLong(film -> film.getLikedUsers().size()))
                .limit(TOP_LIMIT_N)
                .collect(Collectors.toList());
    }
}
