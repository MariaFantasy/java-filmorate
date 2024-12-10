package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;

@lombok.Data
@lombok.AllArgsConstructor
@lombok.RequiredArgsConstructor
@lombok.EqualsAndHashCode(of = {"id"})
public class Director {
    private Long id;

    @NotBlank(message = "Имя режиссера не может быть пустым.")
    private String name;
}
