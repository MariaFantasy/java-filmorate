package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;

@lombok.Data
@lombok.AllArgsConstructor
@lombok.RequiredArgsConstructor
@lombok.EqualsAndHashCode(of = {"id"})
public class Genre {
    private Integer id;

    @NotBlank(message = "Название жанра не может быть пустым.")
    private String name;
}
