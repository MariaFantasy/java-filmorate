package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@lombok.Data
@lombok.AllArgsConstructor
@lombok.RequiredArgsConstructor
@lombok.EqualsAndHashCode(of = { "id" })
public class User {
    Long id;

    @NotNull
    @NotBlank
    @Email
    String email;

    @NotNull
    @NotBlank
    String login;

    String name;

    @NotNull
    LocalDate birthday;
}
