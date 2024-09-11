package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@lombok.Data
@lombok.AllArgsConstructor
@lombok.RequiredArgsConstructor
@lombok.EqualsAndHashCode(of = { "id" })
public class User {
    private Long id;

    @NotNull(message = "Email пользователя не может быть пустым")
    @Email(message = "Email пользователя передан в некорректном формате")
    private String email;

    @NotBlank(message = "Логин пользователя не может быть пустым")
    private String login;

    private String name;

    @NotNull(message = "Дата рождения пользователя не может быть пустой")
    private LocalDate birthday;

    @JsonIgnore
    private final Set<Long> friends = new HashSet<>();
}
