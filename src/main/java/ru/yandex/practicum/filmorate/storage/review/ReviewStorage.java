package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {
    public List<Review> findLimited(int count);

    public List<Review> findLimitedByFilm(long filmId, int count);

    public Review findById(long id);

    public Review create(Review review);

    public Review update(Review review);

    public void delete(long id);

    public void setLike(long id, long userId, int like);

    public Review findLikedReview(long reviewId, long userId, int like);
}
