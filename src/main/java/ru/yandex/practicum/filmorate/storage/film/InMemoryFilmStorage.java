package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
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
        return films.get(id);
    }

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    private long getNextId() {
        return ++filmCounter;
    }
}
