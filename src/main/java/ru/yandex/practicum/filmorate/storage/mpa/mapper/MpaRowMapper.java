package ru.yandex.practicum.filmorate.storage.film.mapper;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RatingRowMapper implements RowMapper<Integer> {
    @Override
    public Integer mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Integer rating_id = resultSet.getInt("rating_id");
        return rating_id;
    }
}
