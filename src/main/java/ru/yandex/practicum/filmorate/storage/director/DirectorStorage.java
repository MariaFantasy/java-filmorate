package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface DirectorStorage {

    public Director create(Director director);

    public Director update(Director director);

    public Director delete(Director director);

    public Director findById(Long id);

    public Collection<Director> findAll();

    public void loadDirectors(Collection<Film> films);
}
