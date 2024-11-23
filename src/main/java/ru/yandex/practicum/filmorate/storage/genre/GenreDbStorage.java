package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.mapper.GenreRowMapper;

import java.util.Collection;

@Repository("genreDbStorage")
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbc;
    private final GenreRowMapper mapper;

    private static final String FIND_ALL_QUERY = "SELECT genre_id, name FROM genre ORDER BY genre_id";
    private static final String FIND_BY_ID_QUERY = "SELECT genre_id, name FROM genre WHERE genre_id = ?";

    @Override
    public Genre findById(Integer id) {
        try {
            Genre genre = jdbc.queryForObject(FIND_BY_ID_QUERY, mapper, id);
            return genre;
        } catch (EmptyResultDataAccessException ignored) {
            throw new NotFoundException("Жанр с id = " + id + " не найден.");
        }
    }

    @Override
    public Collection<Genre> findAll() {
        return jdbc.query(FIND_ALL_QUERY, mapper);
    }

}
