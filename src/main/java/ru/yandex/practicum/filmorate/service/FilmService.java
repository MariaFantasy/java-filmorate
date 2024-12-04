package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DatabaseException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class FilmService {
    private static final long TOP_LIMIT_N = 10;

    private final FilmStorage filmStorage;
    private final UserService userService;
    private final GenreService genreService;
    private final MpaService mpaService;
    private final DirectorService directorService;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, UserService userService, GenreService genreService, MpaService mpaService, DirectorService directorService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
        this.genreService = genreService;
        this.mpaService = mpaService;
        this.directorService = directorService;
    }

    public Collection<Film> findAll() {
        Collection<Film> films = filmStorage.findAll();
        genreService.loadGenres(films);
        directorService.loadDirectors(films);
        return films;
    }

    public Film findById(Long filmId) {
        Film film = filmStorage.findById(filmId);
        genreService.loadGenres(Collections.singletonList(film));
        directorService.loadDirectors(Collections.singletonList(film));
        return film;
    }

    public Film create(Film film) {
        if (film.getMpa() != null) {
            try {
                mpaService.findById(film.getMpa().getId());
            } catch (NotFoundException e) {
                throw new DatabaseException(e.getMessage());
            }
        }
        if (film.getDirectors() != null) {
            for (Director director : film.getDirectors()) {
                try {
                    directorService.findById(director.getId());
                } catch (NotFoundException e) {
                    throw new DatabaseException(e.getMessage());
                }
            }
        }
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                try {
                    genreService.findById(genre.getId());
                } catch (NotFoundException e) {
                    throw new DatabaseException(e.getMessage());
                }
            }
        }
        filmStorage.create(film);
        return findById(film.getId());
    }

    public Film update(Film film) {
        if (film.getMpa() != null) {
            try {
                mpaService.findById(film.getMpa().getId());
            } catch (NotFoundException e) {
                throw new DatabaseException(e.getMessage());
            }
        }
        if (film.getDirectors() != null) {
            for (Director director : film.getDirectors()) {
                try {
                    directorService.findById(director.getId());
                } catch (NotFoundException e) {
                    throw new DatabaseException(e.getMessage());
                }
            }
        }
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                try {
                    genreService.findById(genre.getId());
                } catch (NotFoundException e) {
                    throw new DatabaseException(e.getMessage());
                }
            }
        }
        filmStorage.update(film);
        return findById(film.getId());
    }

    public Film likeFilm(Long filmId, Long userId) {
        final Film film = filmStorage.findById(filmId);
        final User user = userService.findById(userId);
        if (film == null) {
            throw new NotFoundException("Фильм с id = " + filmId + " не найден.");
        }
        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден.");
        }
        filmStorage.addLike(film, user);
        return film;
    }

    public Film unlikeFilm(Long filmId, Long userId) {
        final Film film = filmStorage.findById(filmId);
        final User user = userService.findById(userId);
        if (film == null) {
            throw new NotFoundException("Фильм с id = " + filmId + " не найден.");
        }
        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден.");
        }
        filmStorage.deleteLike(film, user);
        return film;
    }

    public List<Film> getTopFilmsByLike(Long count) {
        if (count == null) {
            count = TOP_LIMIT_N;
        }
        List<Film> films = filmStorage.getTopFilmsByLike(count);
        genreService.loadGenres(films);
        directorService.loadDirectors(films);
        return films;
    }

    public List<Film> getByDirector(Long directorId, String sortType) {
        List<Film> films = filmStorage.getByDirector(directorId);
        genreService.loadGenres(films);
        directorService.loadDirectors(films);
        filmStorage.loadLikes(films);
        films.sort(new Comparator<Film>() {
            @Override
            public int compare(Film film, Film otherFilm) {
                if (sortType.equals("year")) {
                    return Integer.compare(film.getReleaseDate().getYear(), otherFilm.getReleaseDate().getYear());
                }
                if (sortType.equals("likes")) {
                    return -Long.compare(film.getLikedUsers().size(), otherFilm.getLikedUsers().size());
                }
                return 0;
            }
        });
        return films;
    }
}
