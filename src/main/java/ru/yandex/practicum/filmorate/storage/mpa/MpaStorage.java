package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

public interface MpaStorage {

    public Mpa findById(Integer id);

    public Collection<Mpa> findAll();
}
