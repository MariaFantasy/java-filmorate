package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.*;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.friendship.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.friendship.InMemoryFriendshipStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.genre.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.mpa.mapper.MpaRowMapper;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.HashSet;
import java.util.Set;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {
    public static FriendshipStorage friendshipStorage = new InMemoryFriendshipStorage();
    public static UserStorage userStorage = new InMemoryUserStorage();
    public static GenreStorage genreStorage = new GenreDbStorage(new JdbcTemplate(), new GenreRowMapper());
    public static MpaStorage mpaStorage = new MpaDbStorage(new JdbcTemplate(), new MpaRowMapper());
    public static FilmStorage filmStorage = new InMemoryFilmStorage();

    public static FriendshipService friendshipService = new FriendshipService(friendshipStorage);
    public static UserService userService = new UserService(userStorage, friendshipService);
    public static GenreService genreService = new GenreService((genreStorage));
    public static MpaService mpaService = new MpaService((mpaStorage));
    public static FilmService filmService = new FilmService(filmStorage, userService, genreService, mpaService);

    public static FilmController filmController = new FilmController(filmService);
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void testThrowsIfFilmNameIsEmpty() {
        Film film = new Film(1L, null, new Mpa(), new HashSet<Genre>(), "BBB", LocalDate.of(2024, 8, 3), 60, new HashSet<>());
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Validation errors expected");
    }

    @Test
    public void testThrowsIfFilmNameIsBlank() {
        Film film = new Film(1L, "   ", new Mpa(), new HashSet<Genre>(), "BBB", LocalDate.of(2024, 8, 3), 60, new HashSet<>());
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Validation errors expected");
    }

    @Test
    public void testThrowsIfFilmDescriptionLengthMoreThan200() {
        Film film = new Film(1L, "ABC", new Mpa(), new HashSet<Genre>(), "B".repeat(201), LocalDate.of(2024, 8, 3), 60, new HashSet<>());
        assertThrows(ConditionsNotMetException.class, () -> filmController.create(film), "ConditionsNotMetException was expected");
    }

    @Test
    public void testThrowsIfFilmReleaseDateIsBefore28Dec1895() {
        Film film = new Film(1L, "ABC", new Mpa(), new HashSet<Genre>(), "BBB", LocalDate.of(1895, 12, 27), 60, new HashSet<>());
        assertThrows(ConditionsNotMetException.class, () -> filmController.create(film), "ConditionsNotMetException was expected");
    }

    @Test
    public void testThrowsIfFilmDurationIsPositive() {
        Film film = new Film(1L, "ABC", new Mpa(), new HashSet<Genre>(), "BBB", LocalDate.of(2024, 8, 3), 0, new HashSet<>());
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Validation errors expected");
    }

    @Test
    public void testThrowsIfFilmEmpty() {
        Film film = new Film();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Validation errors expected");
    }

//    @Test
//    public void testFilmCreatedIfAllFieldsNormal() {
//        Film film = new Film(1L, "ABC", new Mpa(), new HashSet<Genre>(), "BBB", LocalDate.of(2024, 8, 3), 60, new HashSet<>());
//        Film createdFilm = filmController.create(film);
//        assertEquals(film.getName(), createdFilm.getName(), "Expected equals films");
//    }
//
//    @Test
//    public void testAddLikeNormal() {
//        Film film = new Film(1L, "ABC", new Mpa(), new HashSet<Genre>(), "BBB", LocalDate.of(2024, 8, 3), 60, new HashSet<>());
//        User user = new User(1L, "myemail@gmail.com", "login", "name", LocalDate.of(2024, 1, 1));
//        Film createdFilm = filmController.create(film);
//        User createdUser = userStorage.create(user);
//        filmController.addLike(createdFilm.getId(), createdUser.getId());
//        assertTrue(createdFilm.getLikedUsers().contains(createdUser.getId()));
//    }
//
//    @Test
//    public void testAddLikeNotFoundUser() {
//        Film film = new Film(1L, "ABC", new Mpa(), new HashSet<Genre>(), "BBB", LocalDate.of(2024, 8, 3), 60, new HashSet<>());
//        User user = new User(100000L, "myemail@gmail.com", "login", "name", LocalDate.of(2024, 1, 1));
//        Film createdFilm = filmController.create(film);
//        assertThrows(NotFoundException.class, () ->  filmController.addLike(createdFilm.getId(), user.getId()));
//    }

    @Test
    public void testAddLikeNotFoundFilm() {
        Film film = new Film(10000L, "ABC", new Mpa(), new HashSet<Genre>(), "BBB", LocalDate.of(2024, 8, 3), 60, new HashSet<>());
        User user = new User(1L, "myemail@gmail.com", "login", "name", LocalDate.of(2024, 1, 1));
        User createdUser = userStorage.create(user);
        assertThrows(NotFoundException.class, () ->  filmController.addLike(film.getId(), createdUser.getId()));
    }

//    @Test
//    public void testDeleteLikeNormal() {
//        Film film = new Film(1L, "ABC", new Mpa(), new HashSet<Genre>(), "BBB", LocalDate.of(2024, 8, 3), 60, new HashSet<>());
//        User user = new User(1L, "myemail@gmail.com", "login", "name", LocalDate.of(2024, 1, 1));
//        Film createdFilm = filmController.create(film);
//        User createdUser = userStorage.create(user);
//        filmController.addLike(createdFilm.getId(), createdUser.getId());
//        filmController.deleteLike(createdFilm.getId(), createdUser.getId());
//        assertFalse(createdFilm.getLikedUsers().contains(createdUser.getId()));
//    }

    @Test
    public void testDeleteLikeNormalNotFoundFilm() {
        Film film = new Film(10000L, "ABC", new Mpa(), new HashSet<Genre>(), "BBB", LocalDate.of(2024, 8, 3), 60, new HashSet<>());
        User user = new User(1L, "myemail@gmail.com", "login", "name", LocalDate.of(2024, 1, 1));
        User createdUser = userStorage.create(user);
        assertThrows(NotFoundException.class, () -> filmController.deleteLike(film.getId(), createdUser.getId()));
    }

//    @Test
//    public void testDeleteLikeNormalNotFoundUser() {
//        Film film = new Film(10000L, "ABC", new Mpa(), new HashSet<Genre>(), "BBB", LocalDate.of(2024, 8, 3), 60, new HashSet<>());
//        User user = new User(10000L, "myemail@gmail.com", "login", "name", LocalDate.of(2024, 1, 1));
//        Film createdFilm = filmController.create(film);
//        assertThrows(NotFoundException.class, () -> filmController.deleteLike(createdFilm.getId(), user.getId()));
//    }
//
//    @Test
//    public void testDeleteLikeWhenNotFoundLikesInFilm() {
//        Film film = new Film(1L, "ABC", new Mpa(), new HashSet<Genre>(), "BBB", LocalDate.of(2024, 8, 3), 60, new HashSet<>());
//        User user = new User(1L, "myemail@gmail.com", "login", "name", LocalDate.of(2024, 1, 1));
//        Film createdFilm = filmController.create(film);
//        User createdUser = userStorage.create(user);
//        filmController.deleteLike(createdFilm.getId(), createdUser.getId());
//        assertEquals(0, createdFilm.getLikedUsers().size());
//    }
//
//    @Test
//    public void testLimit1InGetPopularFilms() {
//        Film film1 = new Film(1L, "ABC", new Mpa(), new HashSet<Genre>(), "BBB", LocalDate.of(2024, 8, 3), 60, new HashSet<>());
//        Film film2 = new Film(1L, "ABC", new Mpa(), new HashSet<Genre>(), "BBB", LocalDate.of(2024, 8, 3), 60, new HashSet<>());
//        User user1 = new User(1L, "myemail@gmail.com", "login", "name", LocalDate.of(2024, 1, 1));
//        User user2 = new User(1L, "myemail2@gmail.com", "login2", "name2", LocalDate.of(2024, 1, 1));
//        Film createdFilm1 = filmController.create(film1);
//        Film createdFilm2 = filmController.create(film2);
//        User createdUser1 = userStorage.create(user1);
//        User createdUser2 = userStorage.create(user2);
//        filmController.addLike(createdFilm1.getId(), createdUser1.getId());
//        filmController.addLike(createdFilm1.getId(), createdUser2.getId());
//        filmController.addLike(createdFilm2.getId(), createdUser1.getId());
//        assertEquals(1, filmController.getPopular(1L).size());
//        assertTrue(filmController.getPopular(1L).contains(film1));
//    }
//
//    @Test
//    public void testNoLimitInGetPopularFilms() {
//        Film film1 = new Film(1L, "ABC", new Mpa(), new HashSet<Genre>(), "BBB", LocalDate.of(2024, 8, 3), 60, new HashSet<>());
//        Film film2 = new Film(1L, "ABC", new Mpa(), new HashSet<Genre>(), "BBB", LocalDate.of(2024, 8, 3), 60, new HashSet<>());
//        Film film3 = new Film(1L, "ABC", new Mpa(), new HashSet<Genre>(), "BBB", LocalDate.of(2024, 8, 3), 60, new HashSet<>());
//        Film film4 = new Film(1L, "ABC", new Mpa(), new HashSet<Genre>(), "BBB", LocalDate.of(2024, 8, 3), 60, new HashSet<>());
//        Film film5 = new Film(1L, "ABC", new Mpa(), new HashSet<Genre>(), "BBB", LocalDate.of(2024, 8, 3), 60, new HashSet<>());
//        Film film6 = new Film(1L, "ABC", new Mpa(), new HashSet<Genre>(), "BBB", LocalDate.of(2024, 8, 3), 60, new HashSet<>());
//        Film film7 = new Film(1L, "ABC", new Mpa(), new HashSet<Genre>(), "BBB", LocalDate.of(2024, 8, 3), 60, new HashSet<>());
//        Film film8 = new Film(1L, "ABC", new Mpa(), new HashSet<Genre>(), "BBB", LocalDate.of(2024, 8, 3), 60, new HashSet<>());
//        Film film9 = new Film(1L, "ABC", new Mpa(), new HashSet<Genre>(), "BBB", LocalDate.of(2024, 8, 3), 60, new HashSet<>());
//        Film film10 = new Film(1L, "ABC", new Mpa(), new HashSet<Genre>(), "BBB", LocalDate.of(2024, 8, 3), 60, new HashSet<>());
//        Film film11 = new Film(1L, "ABC", new Mpa(), new HashSet<Genre>(), "BBB", LocalDate.of(2024, 8, 3), 60, new HashSet<>());
//        Film createdFilm1 = filmController.create(film1);
//        Film createdFilm2 = filmController.create(film2);
//        Film createdFilm3 = filmController.create(film3);
//        Film createdFilm4 = filmController.create(film4);
//        Film createdFilm5 = filmController.create(film5);
//        Film createdFilm6 = filmController.create(film6);
//        Film createdFilm7 = filmController.create(film7);
//        Film createdFilm8 = filmController.create(film8);
//        Film createdFilm9 = filmController.create(film9);
//        Film createdFilm10 = filmController.create(film10);
//        Film createdFilm11 = filmController.create(film11);
//        assertEquals(10, filmController.getPopular(null).size());
//    }
}
