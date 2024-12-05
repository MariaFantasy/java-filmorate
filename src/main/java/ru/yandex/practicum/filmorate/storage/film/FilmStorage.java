package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {

    public Film create(Film film);

    public Film update(Film film);

    public Film delete(Film film);

    public Film findById(Long id);

    public Collection<Film> findAll();

    public void addLike(Film film, User user);

    public void deleteLike(Film film, User user);

    public List<Film> getTopFilmsByLike(Long count);

    default List<Film> getTopFilmsByLike(Long count, Integer genreId, Integer year) {
        return getTopFilmsByLike(count);   // ignore genreId & year for in-memory storage
    }
}
