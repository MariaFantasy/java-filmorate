package ru.yandex.practicum.filmorate.storage.film.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.LinkedHashSet;

@Component
@RequiredArgsConstructor
public class FilmRowMapper implements RowMapper<Film> {
    private final MpaService mpaService;
    private final GenreService genreService;

    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getLong("film_id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
        film.setDuration(resultSet.getInt("duration"));

        film.setMpa(mpaService.findById(resultSet.getInt("rating_id")));

        Array genresArray = resultSet.getArray("genres");
        if (genresArray != null) {
            Object[] genresIds = (Object[]) genresArray.getArray();
            film.setGenres(Arrays.stream(genresIds)
                    .map(id -> (Integer) id)
                    .map(genreService::findById)
                    .collect(Collectors.toCollection(LinkedHashSet::new)));
        }

        return film;
    }
}
