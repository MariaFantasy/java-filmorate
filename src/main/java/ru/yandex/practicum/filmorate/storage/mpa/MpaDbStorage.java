package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.mapper.MpaRowMapper;

import java.util.Collection;

@Repository("mpaStorage")
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {
    private static final String FIND_ALL_QUERY = "SELECT rating_id, name FROM rating ORDER BY rating_id";
    private static final String FIND_BY_ID_QUERY = "SELECT rating_id, name FROM rating WHERE rating_id = ?";

    private final JdbcTemplate jdbc;
    private final MpaRowMapper mapper;

    @Override
    public Mpa findById(Integer id) {
        try {
            Mpa mpa = jdbc.queryForObject(FIND_BY_ID_QUERY, mapper, id);
            return mpa;
        } catch (EmptyResultDataAccessException ignored) {
            throw new NotFoundException("Рейтинг с id = " + id + " не найден.");
        }
    }

    @Override
    public Collection<Mpa> findAll() {
        return jdbc.query(FIND_ALL_QUERY, mapper);
    }
}
