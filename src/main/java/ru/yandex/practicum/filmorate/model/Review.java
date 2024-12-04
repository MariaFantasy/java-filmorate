package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@lombok.Data
@lombok.AllArgsConstructor
@lombok.RequiredArgsConstructor
@lombok.EqualsAndHashCode(of = {"reviewId"})
public class Review {
    private Long reviewId;

    @NotBlank(message = "Отзыв не может быть пустым")
    private String content;

    @NotNull
    private Boolean isPositive;

    private Long filmId;

    private Long userId;

    private int useful = 0;
}
