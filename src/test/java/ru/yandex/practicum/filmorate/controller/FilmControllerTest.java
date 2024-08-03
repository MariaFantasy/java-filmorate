package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmControllerTest {
    public static FilmController filmController = new FilmController();

    @Test
    public void testThrowsIfFilmNameIsEmpty() {
        Film film = new Film(1L, null, "BBB", LocalDate.of(2024, 8, 3), Duration.ofMinutes(60));
        assertThrows(ConditionsNotMetException.class, () -> filmController.create(film), "ConditionsNotMetException was expected");
    }

    @Test
    public void testThrowsIfFilmNameIsBlank() {
        Film film = new Film(1L, "   ", "BBB", LocalDate.of(2024, 8, 3), Duration.ofMinutes(60));
        assertThrows(ConditionsNotMetException.class, () -> filmController.create(film), "ConditionsNotMetException was expected");
    }

    @Test
    public void testThrowsIfFilmDescriptionLengthMoreThan200() {
        Film film = new Film(1L, "ABC", "B".repeat(201), LocalDate.of(2024, 8, 3), Duration.ofMinutes(60));
        assertThrows(ConditionsNotMetException.class, () -> filmController.create(film), "ConditionsNotMetException was expected");
    }

    @Test
    public void testThrowsIfFilmReleaseDateIsBefore28Dec1895() {
        Film film = new Film(1L, "ABC", "BBB", LocalDate.of(1895, 12, 27), Duration.ofMinutes(60));
        assertThrows(ConditionsNotMetException.class, () -> filmController.create(film), "ConditionsNotMetException was expected");
    }

    @Test
    public void testThrowsIfFilmDurationIsPositive() {
        Film film = new Film(1L, "ABC", "BBB", LocalDate.of(2024, 8, 3), Duration.ZERO);
        assertThrows(ConditionsNotMetException.class, () -> filmController.create(film), "ConditionsNotMetException was expected");
    }

    @Test
    public void testThrowsIfFilmEmpty() {
        Film film = new Film();
        assertThrows(ConditionsNotMetException.class, () -> filmController.create(film), "ConditionsNotMetException was expected");
    }

    @Test
    public void testFilmCreatedIfAllFieldsNormal() {
        Film film = new Film(1L, "ABC", "BBB", LocalDate.of(2024, 8, 3), Duration.ofMinutes(60));
        Film createdFilm = filmController.create(film);
        assertEquals(film.getName(), createdFilm.getName(), "Expected equals films");
    }
}
