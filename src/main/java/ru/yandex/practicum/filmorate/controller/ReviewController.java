package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.Collection;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private static final Logger log = LoggerFactory.getLogger(ReviewController.class);
    private final ReviewService reviewService;

    @GetMapping
    public Collection<Review> findByParameter(@RequestParam Long filmId,
                                              @RequestParam(defaultValue = "10") int count) {
        log.info("Пришел GET запрос /reviews");
        Collection<Review> allReviews = reviewService.findByParameter(filmId, count);
        log.info("Отправлен ответ GET /reviews с телом: {}", allReviews);
        return allReviews;
    }

    @GetMapping("/{reviewId}")
    public Review findById(@PathVariable Long reviewId) {
        log.info("Пришел GET запрос /reviews/{}", reviewId);
        final Review review = reviewService.findById(reviewId);
        log.info("Отправлен ответ GET /reviews/{} с телом: {}", reviewId, review);
        return review;
    }

    @PostMapping
    public Review create(@Valid @RequestBody Review review) {
        log.info("Пришел POST запрос /reviews с телом: {}", review);
        Review createdReview = reviewService.create(review);
        log.info("Отправлен ответ POST /reviews с телом: {}", createdReview);
        return createdReview;
    }

    @PutMapping
    public Review update(@Valid @RequestBody Review review) {
        log.info("Пришел PUT запрос /reviews с телом: {}", review);
        final Long reviewId = review.getReviewId();

        if (reviewId == null) {
            log.info("Запрос PUT /reviews обработан не был по причине: Id должен быть указан");
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        Review updatedReview = reviewService.update(review);
        log.info("Отправлен ответ PUT /reviews с телом: {}", updatedReview);
        return updatedReview;
    }

    @DeleteMapping("/{reviewId}")
    public Review delete(@PathVariable Long reviewId) {
        log.info("Пришел DELETE запрос /reviews/{}", reviewId);
        Review review = reviewService.delete(reviewId);
        log.info("Отправлен ответ DELETE /reviews/{} с телом: {}", reviewId, review);
        return review;
    }

    @PutMapping("/{reviewId}/like/{userId}")
    public Review addLike(@PathVariable Long reviewId, @PathVariable Long userId) {
        log.info("Пришел PUT запрос /reviews/{}/like/{}", reviewId, userId);

        Review review = reviewService.addLike(reviewId, userId);
        log.info("Отправлен ответ PUT /reviews/{}/like/{} с телом: {}", reviewId, userId, review);
        return review;
    }

    @DeleteMapping("/{reviewId}/like/{userId}")
    public Review removeLike(@PathVariable Long reviewId, @PathVariable Long userId) {
        log.info("Пришел DELETE запрос /reviews/{}/like/{}", reviewId, userId);

        Review review = reviewService.removeLike(reviewId, userId);
        log.info("Отправлен ответ DELETE /reviews/{}/like/{} с телом: {}", reviewId, userId, review);
        return review;
    }

    @PutMapping("/{reviewId}/dislike/{userId}")
    public Review addDislike(@PathVariable Long reviewId, @PathVariable Long userId) {
        log.info("Пришел PUT запрос /reviews/{}/dislike/{}", reviewId, userId);

        Review review = reviewService.addDislike(reviewId, userId);
        log.info("Отправлен ответ PUT /reviews/{}/dislike/{} с телом: {}", reviewId, userId, review);
        return review;
    }

    @DeleteMapping("/{reviewId}/dislike/{userId}")
    public Review removeDislike(@PathVariable Long reviewId, @PathVariable Long userId) {
        log.info("Пришел DELETE запрос /reviews/{}/dislike/{}", reviewId, userId);

        Review review = reviewService.removeLike(reviewId, userId);
        log.info("Отправлен ответ DELETE /reviews/{}/dislike/{} с телом: {}", reviewId, userId, review);
        return review;
    }
}
