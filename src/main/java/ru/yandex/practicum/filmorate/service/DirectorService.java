package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.Collection;

@Service
public class DirectorService {
    private final DirectorStorage directorStorage;

    public DirectorService(@Qualifier("directorDbStorage") DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    public Collection<Director> findAll() {
        return directorStorage.findAll();
    }

    public Director findById(Long directorId) {
        return directorStorage.findById(directorId);
    }

    public Director create(Director director) {
        directorStorage.create(director);
        return findById(director.getId());
    }

    public Director update(Director director) {
        directorStorage.update(director);
        return findById(director.getId());
    }

    public Director delete(Director director) {
        directorStorage.delete(director);
        return director;
    }
}
