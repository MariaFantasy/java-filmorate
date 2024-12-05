package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {

    Film create(Film film);

    Film update(Film film);

    Film delete(Film film);

    Film findById(Long id);

    Collection<Film> findAll();

     void addLike(Film film, User user);

    void deleteLike(Film film, User user);

    List<Film> getTopFilmsByLike(Long count);
}
