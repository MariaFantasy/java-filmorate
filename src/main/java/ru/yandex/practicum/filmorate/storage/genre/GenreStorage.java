package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

public interface GenreStorage {

    public Genre findById(Integer id);

    public Collection<Genre> findAll();

    public void loadGenres(Collection<Film> films);
}
