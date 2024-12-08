package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DatabaseException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.types.EventType;
import ru.yandex.practicum.filmorate.model.types.Operation;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;
    private final GenreService genreService;
    private final MpaService mpaService;
    private final DirectorService directorService;
    private final FeedService feedService;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, UserService userService, GenreService genreService, MpaService mpaService, DirectorService directorService, FeedService feedService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
        this.genreService = genreService;
        this.mpaService = mpaService;
        this.directorService = directorService;
        this.feedService = feedService;
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
        feedService.create(userId, filmId, EventType.LIKE, Operation.ADD);

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
        feedService.create(userId, filmId, EventType.LIKE, Operation.REMOVE);

        return film;
    }

    public List<Film> getTopFilmsByLike(Long count, Integer genreId, Integer year) {
        List<Film> films = filmStorage.getTopFilmsByLike(count, genreId, year);
        genreService.loadGenres(films);
        directorService.loadDirectors(films);
        return films;
    }

    public List<Film> getCommonUserFilms(Long userId, Long otherUserId) {
        if (userService.findById(userId) == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден.");
        }
        if (userService.findById(otherUserId) == null) {
            throw new NotFoundException("Пользователь с id = " + otherUserId + " не найден.");
        }
        List<Film> films = filmStorage.getCommonUserFilms(userId, otherUserId);
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

    public void delete(Film film) {
        filmStorage.delete(film);
    }

    public Collection<Film> search(String query, String by) {
        String[] searchFields = by.split(",");
        Set<Film> resultFilms = new LinkedHashSet<>();

        if (Arrays.asList(searchFields).contains("title")) {
            resultFilms.addAll(filmStorage.searchFilmsByTitle(query));
        }

        if (Arrays.asList(searchFields).contains("director")) {
            resultFilms.addAll(filmStorage.searchFilmsByDirector(query));
        }

        genreService.loadGenres(resultFilms);
        directorService.loadDirectors(resultFilms);

        List<Film> sortedFilms = new ArrayList<>(resultFilms);
        filmStorage.loadLikes(sortedFilms);
        sortedFilms.sort((film1, film2) -> Integer.compare(film2.getLikedUsers().size(), film1.getLikedUsers().size()));

        return sortedFilms;

    }

    public List<Film> getRecommendationByUserId(Long userID) {
        return filmStorage.getRecommendationByUserId(userID);
    }
}
