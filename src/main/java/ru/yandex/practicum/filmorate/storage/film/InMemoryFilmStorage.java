package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component("inMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private long filmCounter = 0;

    @Override
    public Film create(Film film) {
        final long filmId = getNextId();
        film.setId(filmId);
        films.put(filmId, film);
        return film;
    }

    @Override
    public Film update(Film film) {
        final Long filmId = film.getId();
        films.put(filmId, film);
        return film;
    }

    @Override
    public Film delete(Film film) {
        final Long filmId = film.getId();
        films.remove(filmId);
        return film;
    }

    @Override
    public Film findById(Long id) {
        Film film = films.get(id);
        if (film == null) {
            throw new NotFoundException("Фильм с id = " + id + " не найден.");
        }
        return film;
    }

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public void addLike(Film film, User user) {
        film.getLikedUsers().add(user.getId());
    }

    @Override
    public void deleteLike(Film film, User user) {
        film.getLikedUsers().remove(user.getId());
    }

    @Override
    public List<Film> getTopFilmsByLike(Long count) {
        return findAll().stream()
                .sorted(Comparator.comparingLong(film -> -film.getLikedUsers().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public List<Film> getRecommendationByUserId(Long userID) {
        return List.of();
    }

    private long getNextId() {
        return ++filmCounter;
    }
}
