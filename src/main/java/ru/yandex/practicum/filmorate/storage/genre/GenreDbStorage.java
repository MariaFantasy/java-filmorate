package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.mapper.GenreRowMapper;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.function.UnaryOperator.identity;

@Repository("genreDbStorage")
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private static final String FIND_ALL_QUERY = "SELECT genre_id, name FROM genre ORDER BY genre_id";
    private static final String FIND_BY_ID_QUERY = "SELECT genre_id, name FROM genre WHERE genre_id = ?";

    private final JdbcTemplate jdbc;
    private final GenreRowMapper mapper;

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

    @Override
    public void loadGenres(Collection<Film> films) {
        final Map<Long, Film> filmById = films.stream().collect(Collectors.toMap(Film::getId, identity()));

        String filmsIds = films.stream()
                .map(Film::getId)
                .map(Objects::toString)
                .collect(Collectors.joining(","));
        final String loadGenresQuery = "SELECT fg.film_id, g.genre_id, g.name FROM film_genre AS fg INNER JOIN genre AS g ON fg.genre_id = g.genre_id WHERE fg.film_id IN (" +
                filmsIds + ") ORDER BY fg.genre_id";

        jdbc.query(loadGenresQuery, (rs) -> {
            final Film film = filmById.get(rs.getLong("film_id"));
            Genre genre = mapper.mapRow(rs, 0);
            film.addGenre(genre);
        });
    }
}
