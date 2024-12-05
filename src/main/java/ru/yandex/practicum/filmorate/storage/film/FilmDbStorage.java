package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.film.mapper.FilmRowMapper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.function.UnaryOperator.identity;

@Repository("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbc;
    private final FilmRowMapper mapper;

    private static final String FIND_ALL_QUERY = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.rating_id, r.name as rating_name FROM film AS f LEFT JOIN rating AS r ON f.rating_id = r.rating_id ORDER BY f.film_id";
    private static final String FIND_BY_ID_QUERY = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.rating_id, r.name as rating_name FROM film AS f LEFT JOIN rating AS r ON f.rating_id = r.rating_id WHERE f.film_id = ?";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM film WHERE film_id = ?";
    private static final String UPDATE_BY_ID_QUERY = "UPDATE film SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? WHERE film_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO film (name, description, release_date, duration, rating_id) VALUES (?, ?, ?, ?, ?)";
    private static final String ADD_GENRE_QUERY = "MERGE INTO film_genre (film_id, genre_id) VALUES (?, ?)";
    private static final String DELETE_BY_ID_GENRE_QUERY = "DELETE FROM film_genre WHERE film_id = ?";
    private static final String ADD_DIRECTOR_QUERY = "MERGE INTO film_director (film_id, director_id) VALUES (?, ?)";
    private static final String ADD_LIKE_QUERY = "MERGE INTO film_like (film_id, user_id) VALUES (?, ?)";
    private static final String DELETE_BY_ID_DIRECTOR_QUERY = "DELETE FROM film_director WHERE film_id = ?";
    private static final String FIND_ALL_BY_DIRECTOR_QUERY = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.rating_id, r.name as rating_name FROM film AS f LEFT JOIN rating AS r ON f.rating_id = r.rating_id INNER JOIN film_director AS fd ON f.film_id = fd.film_id WHERE fd.director_id = ?";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM film_like WHERE film_id = ? AND user_id = ?";
    private static final String TOP_LIST_QUERY = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.rating_id, r.name as rating_name, COALESCE(l.likes, 0) AS likes FROM film AS f LEFT JOIN rating AS r ON f.rating_id = r.rating_id LEFT JOIN (SELECT film_id, COUNT(DISTINCT user_id) AS likes FROM film_like GROUP BY film_id) AS l ON f.film_id = l.film_id ORDER BY likes DESC LIMIT ?";

    private static final String FIND_POPULAR_QUERY =
            """
                    SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.rating_id,
                           r.name AS rating_name, fl.likes
                    FROM film AS f
                    LEFT JOIN rating AS r ON f.rating_id = r.rating_id
                    INNER JOIN
                      (SELECT film_id,
                              COUNT(DISTINCT user_id) AS likes
                       FROM film_like
                       GROUP BY film_id) AS fl ON f.film_id = fl.film_id
                    WHERE (? IS NULL
                           OR EXTRACT(YEAR
                                      FROM f.release_date) = ?)
                      AND (? IS NULL
                           OR ? IN
                             (SELECT genre_id
                              FROM film_genre
                              WHERE film_id = f.film_id))
                    ORDER BY fl.likes DESC
                    LIMIT ?""";

    private static final String SEARCH_FILMS_BY_TITLE_SQL =
            "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, " +
                    "r.rating_id AS rating_id, r.name AS rating_name, " +
                    "d.director_id AS director_id, d.name AS director_name, " +
                    "GROUP_CONCAT(g.genre_id) AS genre_ids, GROUP_CONCAT(g.name) AS genre_names " +
                    "FROM film AS f " +
                    "LEFT JOIN rating AS r ON f.rating_id = r.rating_id " +
                    "LEFT JOIN film_genre AS fg ON f.film_id = fg.film_id " +
                    "LEFT JOIN genre AS g ON fg.genre_id = g.genre_id " +
                    "LEFT JOIN film_director AS fd ON f.film_id = fd.film_id " +
                    "LEFT JOIN director AS d ON d.director_id = fd.director_id " +
                    "WHERE LOWER(f.name) LIKE LOWER(?) " +
                    "GROUP BY f.film_id, r.rating_id, d.director_id";

    private static final String SEARCH_FILMS_BY_DIRECTOR_SQL =
            "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, " +
                    "r.rating_id AS rating_id, r.name AS rating_name, " +
                    "d.director_id AS director_id, d.name AS director_name, " +
                    "GROUP_CONCAT(g.genre_id) AS genre_ids, GROUP_CONCAT(g.name) AS genre_names " +
                    "FROM film AS f " +
                    "JOIN film_director AS fd ON f.film_id = fd.film_id " +
                    "JOIN director AS d ON d.director_id = fd.director_id " +
                    "LEFT JOIN rating AS r ON f.rating_id = r.rating_id " +
                    "LEFT JOIN film_genre AS fg ON f.film_id = fg.film_id " +
                    "LEFT JOIN genre AS g ON fg.genre_id = g.genre_id " +
                    "WHERE LOWER(d.name) LIKE LOWER(?) " +
                    "GROUP BY f.film_id, r.rating_id, d.director_id";

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
            if (film.getMpa() == null) {
                ps.setObject(5, null, java.sql.Types.INTEGER);
            } else {
                ps.setObject(5, film.getMpa().getId(), java.sql.Types.INTEGER);
            }
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKeyAs(Long.class);
        film.setId(id);

        if (film.getDirectors() != null) {
            for (Director director : film.getDirectors()) {
                jdbc.update(ADD_DIRECTOR_QUERY, film.getId(), director.getId());
            }
        }
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

        jdbc.update(DELETE_BY_ID_DIRECTOR_QUERY, film.getId());
        if (film.getDirectors() != null) {
            for (Director director : film.getDirectors()) {
                jdbc.update(ADD_DIRECTOR_QUERY, film.getId(), director.getId());
            }
        }

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

    @Override
    public List<Film> getByDirector(Long directorId) {
        return jdbc.query(FIND_ALL_BY_DIRECTOR_QUERY, mapper, directorId);
    }

    @Override
    public void loadLikes(Collection<Film> films) {
        final Map<Long, Film> filmById = films.stream().collect(Collectors.toMap(Film::getId, identity()));

        String filmsIds = films.stream()
                .map(Film::getId)
                .map(Objects::toString)
                .collect(Collectors.joining(","));
        final String loadLikedUsers = "SELECT film_id, user_id FROM film_like WHERE film_id IN ("
                + filmsIds + ")";

        jdbc.query(loadLikedUsers, (rs) -> {
            final Film film = filmById.get(rs.getLong("film_id"));
            Long userId = rs.getLong("user_id");
            film.addLike(userId);
        });
    }

    @Override
    public List<Film> getTopFilmsByLike(Long count, Integer genreId, Integer year) {
        return jdbc.query(FIND_POPULAR_QUERY, mapper, year, year, genreId, genreId, count);
    }

    public List<Film> searchFilmsByTitle(String query) {
        return jdbc.query(SEARCH_FILMS_BY_TITLE_SQL, (rs, rowNum) -> mapFilm(rs), "%" + query + "%");
    }

    public List<Film> searchFilmsByDirector(String query) {
        return jdbc.query(SEARCH_FILMS_BY_DIRECTOR_SQL, (rs, rowNum) -> mapFilm(rs), "%" + query + "%");
    }

    private Film mapFilm(ResultSet rs) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("film_id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));

        Mpa mpa = new Mpa(rs.getInt("rating_id"), rs.getString("rating_name"));
        film.setMpa(mpa);

        String directorIds = String.valueOf(rs.getLong("director_id"));
        String directorNames = rs.getString("director_name");
        if (directorNames != null) {
            String[] ids = directorIds.split(",");
            String[] names = directorNames.split(",");
            for (int i = 0; i < ids.length; i++) {
                Director director = new Director();
                director.setId(Long.valueOf(ids[i]));
                director.setName(names[i]);
                film.addDirector(director);
            }
        }

        String genreIds = rs.getString("genre_ids");
        String genreNames = rs.getString("genre_names");
        if (genreIds != null) {
            String[] ids = genreIds.split(",");
            String[] names = genreNames.split(",");
            for (int i = 0; i < ids.length; i++) {
                Genre genre = new Genre();
                genre.setId(Integer.parseInt(ids[i]));
                genre.setName(names[i]);
                film.addGenre(genre);
            }
        }

        return film;
    }
}
