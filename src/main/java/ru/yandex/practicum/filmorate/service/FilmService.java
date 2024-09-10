package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    private static final long TOP_LIMIT_N = 10;

    private final FilmStorage filmStorage;

    public void likeFilm(Film film, User user) {
        film.getLikedUsers().add(user.getId());
    }

    public void unlikeFilm(Film film, User user) {
        film.getLikedUsers().remove(user.getId());
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
