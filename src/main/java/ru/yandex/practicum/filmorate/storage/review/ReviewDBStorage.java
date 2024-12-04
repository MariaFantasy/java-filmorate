package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.mapper.ReviewRowMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository("reviewDbStorage")
@RequiredArgsConstructor
public class ReviewDBStorage implements ReviewStorage {
    private final JdbcTemplate jdbc;
    private final ReviewRowMapper mapper;

    private static final String FIND_LIMITED_QUERY = "SELECT * FROM film_review ORDER BY useful desc limit ?";
    private static final String FIND_LIMITED_BY_FILM__QUERY = """
            SELECT * FROM film_review
            WHERE film_id = ?
            ORDER BY useful desc limit ?""";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM film_review WHERE review_id = ?";
    private static final String INSERT_QUERY = """
            INSERT INTO film_review (film_id, user_id, content, useful, is_positive)
            VALUES (?, ?, ?, ?, ?)""";
    private static final String UPDATE_BY_ID_QUERY = """
            UPDATE film_review
            SET content = ?, is_positive = ?
            WHERE review_id = ?""";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM film_review WHERE review_id = ?";
    private static final String ADD_LIKE_QUERY = "MERGE INTO film_review_like (review_id, user_id, like_value) VALUES (?, ?, ?)";
    private static final String UPDATE_USEFUL_BY_ID_QUERY = """
            UPDATE film_review
            SET useful = (SELECT SUM(like_value)
            FROM film_review_like
            WHERE review_id = ?)
            WHERE review_id = ?""";
    private static final String FIND_LIKED_REVIEW_QUERY = """
            SELECT fr.*
            FROM film_review as fr
            INNER JOIN film_review_like as frl
            on fr.review_id = frl.review_id
            WHERE frl.review_id = ? AND frl.user_id = ? AND frl.like_value = ?
            """;

    @Override
    public List<Review> findLimited(int count) {
        return jdbc.query(FIND_LIMITED_QUERY, mapper, count);
    }

    @Override
    public List<Review> findLimitedByFilm(long filmId, int count) {
        return jdbc.query(FIND_LIMITED_BY_FILM__QUERY, mapper, filmId, count);
    }

    @Override
    public Review findById(long id) {
        try {
            return jdbc.queryForObject(FIND_BY_ID_QUERY, mapper, id);
        } catch (EmptyResultDataAccessException ignored) {
            throw new NotFoundException("Отзыв с id = " + id + " не найден.");
        }
    }

    @Override
    public Review create(Review review) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, review.getFilmId());
            ps.setObject(2, review.getUserId());
            ps.setObject(3, review.getContent());
            ps.setObject(4, review.getUseful());
            ps.setObject(5, review.getIsPositive());
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKeyAs(Long.class);

        return findById(id);
    }

    @Override
    public Review update(Review review) {
        jdbc.update(UPDATE_BY_ID_QUERY,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());

        return findById(review.getReviewId());
    }

    @Override
    public void delete(long id) {
        jdbc.update(DELETE_BY_ID_QUERY, id);
    }

    @Override
    public void setLike(long id, long userId, int like) {
        jdbc.update(ADD_LIKE_QUERY, id, userId, like);

        jdbc.update(UPDATE_USEFUL_BY_ID_QUERY, id, id);
    }

    @Override
    public Review findLikedReview(long reviewId, long userId, int like) {
        Review review = jdbc.queryForObject(FIND_LIKED_REVIEW_QUERY, mapper, reviewId, userId, like);

        if (review == null) {
            throw new NotFoundException("Отзыв с реакциями не найден");
        }

        return review;
    }

}
