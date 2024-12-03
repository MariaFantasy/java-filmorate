package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;

@lombok.Data
@lombok.AllArgsConstructor
@lombok.RequiredArgsConstructor
@lombok.EqualsAndHashCode(of = {"id"})
public class Mpa {
    private Integer id;

    @NotBlank(message = "Название рейтинга не может быть пустым.")
    private String name;
}
