package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.mapper.DirectorRowMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;

@Repository("directorDbStorage")
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbc;
    private final DirectorRowMapper mapper;

    private static final String FIND_ALL_QUERY = "SELECT director_id, name FROM director ORDER BY director_id";
    private static final String FIND_BY_ID_QUERY = "SELECT director_id, name FROM director WHERE director_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO director (name) VALUES (?)";
    private static final String UPDATE_BY_ID_QUERY = "UPDATE director SET name = ? WHERE director_id = ?";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM director WHERE director_id = ?";

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
}
