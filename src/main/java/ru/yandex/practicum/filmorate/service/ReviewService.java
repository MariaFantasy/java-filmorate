package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.types.EventType;
import ru.yandex.practicum.filmorate.model.types.Operation;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.List;

@Service
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final UserService userService;
    private final FilmService filmService;
    private final FeedService feedService;

    public ReviewService(@Qualifier("reviewDbStorage") ReviewStorage reviewStorage,
                         UserService userService,
                         FilmService filmService,
                         FeedService feedService) {
        this.reviewStorage = reviewStorage;
        this.userService = userService;
        this.filmService = filmService;
        this.feedService = feedService;
    }

    public List<Review> findByParameter(Long filmId, int count) {
        if (filmId == null) {
            return reviewStorage.findLimited(count);
        } else {
            return reviewStorage.findLimitedByFilm(filmId, count);
        }
    }

    public Review findById(Long reviewId) {
        return reviewStorage.findById(reviewId);
    }

    public Review create(Review review) {
        if (review.getUserId() == 0 || review.getFilmId() == 0) {
            throw new ConditionsNotMetException("Не указан пользователь или фильм");
        }

        filmService.findById(review.getFilmId());
        userService.findById(review.getUserId());

        reviewStorage.create(review);
        feedService.create(review.getUserId(), review.getReviewId(), EventType.REVIEW, Operation.ADD);

        return review;
    }

    public Review update(Review review) {
        reviewStorage.findById(review.getReviewId());
        feedService.create(review.getUserId(), review.getReviewId(), EventType.REVIEW, Operation.UPDATE);
        return reviewStorage.update(review);
    }

    public Review delete(long id) {
        Review review = findById(id);
        reviewStorage.delete(id);
        feedService.create(review.getUserId(), review.getReviewId(), EventType.REVIEW, Operation.REMOVE);

        return review;
    }

    public Review addLike(long reviewId, long userId) {
        reviewStorage.findById(reviewId);
        userService.findById(userId);

        reviewStorage.setLike(reviewId, userId, 1);

        return reviewStorage.findById(reviewId);
    }

    public Review addDislike(long reviewId, long userId) {
        reviewStorage.findById(reviewId);
        userService.findById(userId);

        reviewStorage.setLike(reviewId, userId, -1);

        return reviewStorage.findById(reviewId);
    }

    public Review removeLike(long reviewId, long userId) {
        reviewStorage.findById(reviewId);
        userService.findById(userId);

        Review review = reviewStorage.findLikedReview(reviewId, userId, 1);

        reviewStorage.setLike(reviewId, userId, 0);

        return review;
    }

    public Review removeDislike(long reviewId, long userId) {
        reviewStorage.findById(reviewId);
        userService.findById(userId);

        Review review = reviewStorage.findLikedReview(reviewId, userId, -1);

        reviewStorage.setLike(reviewId, userId, 0);

        return review;
    }
}
