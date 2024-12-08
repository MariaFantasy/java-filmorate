package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
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
        return getTopFilmsByLike(count);   // ignore searching by genreId & year for in-memory storage
    }

    default List<Film> getCommonUserFilms(Long thisUserId, Long otherUserId) {
        return new ArrayList<>();  // returns empty list for in-memory but see @Override implementation in FilmDbStorage
    }

    public List<Film> getByDirector(Long directorId);

    public void loadLikes(Collection<Film> films);

    public List<Film> getRecommendationByUserId(Long userID);
}
