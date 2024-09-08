package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Film;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Set;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmControllerTest {
    public static FilmStorage filmStorage = new InMemoryFilmStorage();
    public static FilmService filmService = new FilmService(filmStorage);
    public static UserStorage userStorage = new InMemoryUserStorage();
    public static FilmController filmController = new FilmController(filmStorage, filmService, userStorage);
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void testThrowsIfFilmNameIsEmpty() {
        Film film = new Film(1L, null, "BBB", LocalDate.of(2024, 8, 3), 60);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Validation errors expected");
    }

    @Test
    public void testThrowsIfFilmNameIsBlank() {
        Film film = new Film(1L, "   ", "BBB", LocalDate.of(2024, 8, 3), 60);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Validation errors expected");
    }

    @Test
    public void testThrowsIfFilmDescriptionLengthMoreThan200() {
        Film film = new Film(1L, "ABC", "B".repeat(201), LocalDate.of(2024, 8, 3), 60);
        assertThrows(ConditionsNotMetException.class, () -> filmController.create(film), "ConditionsNotMetException was expected");
    }

    @Test
    public void testThrowsIfFilmReleaseDateIsBefore28Dec1895() {
        Film film = new Film(1L, "ABC", "BBB", LocalDate.of(1895, 12, 27), 60);
        assertThrows(ConditionsNotMetException.class, () -> filmController.create(film), "ConditionsNotMetException was expected");
    }

    @Test
    public void testThrowsIfFilmDurationIsPositive() {
        Film film = new Film(1L, "ABC", "BBB", LocalDate.of(2024, 8, 3), 0);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Validation errors expected");
    }

    @Test
    public void testThrowsIfFilmEmpty() {
        Film film = new Film();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Validation errors expected");
    }

    @Test
    public void testFilmCreatedIfAllFieldsNormal() {
        Film film = new Film(1L, "ABC", "BBB", LocalDate.of(2024, 8, 3), 60);
        Film createdFilm = filmController.create(film);
        assertEquals(film.getName(), createdFilm.getName(), "Expected equals films");
    }
}
