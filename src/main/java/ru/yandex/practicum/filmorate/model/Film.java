package ru.yandex.practicum.filmorate.model;

import java.time.Duration;
import java.time.LocalDate;

@lombok.Data
@lombok.EqualsAndHashCode(of = { "id" })
public class Film {
    Long id;
    String name;
    String description;
    LocalDate releaseDate;
    Duration duration;
}
