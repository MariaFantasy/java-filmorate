package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;

@lombok.Data
@lombok.EqualsAndHashCode(of = { "id" })
public class User {
    Long id;
    String email;
    String login;
    String name;
    LocalDate birthday;
}
