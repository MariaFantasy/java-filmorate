package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.director.mapper.DirectorRowMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.function.UnaryOperator.identity;

@Repository("directorDbStorage")
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {
    private static final String FIND_ALL_QUERY = "SELECT director_id, name FROM director ORDER BY director_id";
    private static final String FIND_BY_ID_QUERY = "SELECT director_id, name FROM director WHERE director_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO director (name) VALUES (?)";
    private static final String UPDATE_BY_ID_QUERY = "UPDATE director SET name = ? WHERE director_id = ?";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM director WHERE director_id = ?";

    private final JdbcTemplate jdbc;
    private final DirectorRowMapper mapper;

    @Override
    public Director create(Director director) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, director.getName());
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKeyAs(Long.class);
        director.setId(id);

        return findById(id);
    }

    @Override
    public Director update(Director director) {
        jdbc.update(UPDATE_BY_ID_QUERY,
                director.getName(),
                director.getId());

        return findById(director.getId());
    }

    @Override
    public Director delete(Director director) {
        jdbc.update(DELETE_BY_ID_QUERY, director.getId());
        return director;
    }

    @Override
    public Director findById(Long id) {
        try {
            Director director = jdbc.queryForObject(FIND_BY_ID_QUERY, mapper, id);
            return director;
        } catch (EmptyResultDataAccessException ignored) {
            throw new NotFoundException("Режиссер с id = " + id + " не найден.");
        }
    }

    @Override
    public Collection<Director> findAll() {
        return jdbc.query(FIND_ALL_QUERY, mapper);
    }

    @Override
    public void loadDirectors(Collection<Film> films) {
        final Map<Long, Film> filmById = films.stream().collect(Collectors.toMap(Film::getId, identity()));

        String filmsIds = films.stream()
                .map(Film::getId)
                .map(Objects::toString)
                .collect(Collectors.joining(","));
        final String loadDirectorsQuery = "SELECT fd.film_id, d.director_id, d.name FROM film_director AS fd INNER JOIN director AS d ON fd.director_id = d.director_id WHERE fd.film_id IN ("
                + filmsIds + ") ORDER BY fd.director_id";

        jdbc.query(loadDirectorsQuery, (rs) -> {
            final Film film = filmById.get(rs.getLong("film_id"));
            Director director = mapper.mapRow(rs, 0);
            film.addDirector(director);
        });
    }
}
