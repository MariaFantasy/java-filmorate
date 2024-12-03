package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.mapper.FilmRowMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;

@Repository("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbc;
    private final FilmRowMapper mapper;

    private static final String FIND_ALL_QUERY = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.rating_id, r.name as rating_name FROM film AS f LEFT JOIN rating AS r ON f.rating_id = r.rating_id";
    private static final String FIND_BY_ID_QUERY = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.rating_id, r.name as rating_name FROM film AS f LEFT JOIN rating AS r ON f.rating_id = r.rating_id WHERE f.film_id = ?";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM film WHERE film_id = ?";
    private static final String UPDATE_BY_ID_QUERY = "UPDATE film SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? WHERE film_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO film (name, description, release_date, duration, rating_id) VALUES (?, ?, ?, ?, ?)";
    private static final String ADD_GENRE_QUERY = "MERGE INTO film_genre (film_id, genre_id) VALUES (?, ?)";
    private static final String DELETE_BY_ID_GENRE_QUERY = "DELETE FROM film_genre WHERE film_id = ?";
    private static final String ADD_LIKE_QUERY = "MERGE INTO film_like (film_id, user_id) VALUES (?, ?)";
    private static final String DELETE_BY_ID_LIKE_QUERY = "DELETE FROM film_like WHERE film_id = ?";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM film_like WHERE film_id = ? AND user_id = ?";
    private static final String TOP_LIST_QUERY = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.rating_id, r.name as rating_name, l.likes FROM film AS f LEFT JOIN rating AS r ON f.rating_id = r.rating_id INNER JOIN (SELECT film_id, COUNT(DISTINCT user_id) AS likes FROM film_like GROUP BY film_id) AS l ON f.film_id = l.film_id ORDER BY likes DESC LIMIT ?";

    @Override
    public Film create(Film film) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, film.getName());
            ps.setObject(2, film.getDescription());
            ps.setObject(3, film.getReleaseDate());
            ps.setObject(4, film.getDuration());
            ps.setObject(5, film.getMpa().getId(), java.sql.Types.INTEGER);
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKeyAs(Long.class);
        film.setId(id);

        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                jdbc.update(ADD_GENRE_QUERY, film.getId(), genre.getId());
            }
        }

        return findById(id);
    }

    @Override
    public Film update(Film film) {
        jdbc.update(UPDATE_BY_ID_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                (film.getMpa() != null) ? film.getMpa().getId() : null,
                film.getId());

        jdbc.update(DELETE_BY_ID_GENRE_QUERY, film.getId());

        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                jdbc.update(ADD_GENRE_QUERY, film.getId(), genre.getId());
            }
        }

        return findById(film.getId());
    }

    @Override
    public Film delete(Film film) {
        jdbc.update(DELETE_BY_ID_QUERY, film.getId());
        jdbc.update(DELETE_BY_ID_GENRE_QUERY, film.getId());
        jdbc.update(DELETE_BY_ID_LIKE_QUERY, film.getId());
        return film;
    }

    @Override
    public Film findById(Long id) {
        try {
            Film film = jdbc.queryForObject(FIND_BY_ID_QUERY, mapper, id);
            return film;
        } catch (EmptyResultDataAccessException ignored) {
            throw new NotFoundException("Фильм с id = " + id + " не найден.");
        }
    }

    @Override
    public Collection<Film> findAll() {
        return jdbc.query(FIND_ALL_QUERY, mapper);
    }

    @Override
    public void addLike(Film film, User user) {
        jdbc.update(ADD_LIKE_QUERY, film.getId(), user.getId());
    }

    @Override
    public void deleteLike(Film film, User user) {
        jdbc.update(DELETE_LIKE_QUERY, film.getId(), user.getId());
    }

    @Override
    public List<Film> getTopFilmsByLike(Long count) {
        return jdbc.query(TOP_LIST_QUERY, mapper, count);
    }
}
