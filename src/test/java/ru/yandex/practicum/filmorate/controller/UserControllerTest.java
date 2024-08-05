package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.User;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import java.util.Set;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {
    public static UserController userController = new UserController();
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void testThrowsIfEmailIsEmpty() {
        User user = new User(1L, null, "login", "name", LocalDate.of(2024, 1, 1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Validation errors expected");
    }

    @Test
    public void testThrowsIfEmailIsBlank() {
        User user = new User(1L, "  ", "login", "name", LocalDate.of(2024, 1, 1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Validation errors expected");
    }

    @Test
    public void testThrowsIfEmailNotContainsAtSymbol() {
        User user = new User(1L, "myemailgmail.com", "login", "name", LocalDate.of(2024, 1, 1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Validation errors expected");
    }

    @Test
    public void testThrowsIfLoginIsEmpty() {
        User user = new User(1L, "myemail@gmail.com", null, "name", LocalDate.of(2024, 1, 1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Validation errors expected");
    }

    @Test
    public void testThrowsIfLoginIsBlank() {
        User user = new User(1L, "myemail@gmail.com", "   ", "name", LocalDate.of(2024, 1, 1));
        assertThrows(ConditionsNotMetException.class, () -> userController.create(user), "ConditionsNotMetException was expected");
    }

    @Test
    public void testThrowsIfLoginHasSpaces() {
        User user = new User(1L, "myemail@gmail.com", "great login", "name", LocalDate.of(2024, 1, 1));
        assertThrows(ConditionsNotMetException.class, () -> userController.create(user), "ConditionsNotMetException was expected");
    }

    @Test
    public void testSetLoginInNameIfNameIsEmpty() {
        User user = new User(1L, "myemail@gmail.com", "login", null, LocalDate.of(2024, 1, 1));
        User createdUser = userController.create(user);
        assertEquals(createdUser.getLogin(), createdUser.getName(), "Expected login in name");
    }

    @Test
    public void testSetLoginInNameIfNameIsBlank() {
        User user = new User(1L, "myemail@gmail.com", "login", "  ", LocalDate.of(2024, 1, 1));
        User createdUser = userController.create(user);
        assertEquals(createdUser.getLogin(), createdUser.getName(), "Expected login in name");
    }

    @Test
    public void testThrowsIfBirthdayInFuture() {
        User user = new User(1L, "myemail@gmail.com", "login", "name", LocalDate.now().plusDays(1));
        assertThrows(ConditionsNotMetException.class, () -> userController.create(user), "ConditionsNotMetException was expected");
    }

    @Test
    public void testThrowsIfUserEmpty() {
        User user = new User();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Validation errors expected");
    }

    @Test
    public void testUserCreatedIfAllFieldsNormal() {
        User user = new User(1L, "myemail@gmail.com", "login", "name", LocalDate.of(2024, 1, 1));
        User createdUser = userController.create(user);
        assertEquals(user.getName(), createdUser.getName(), "Expected equals users");
    }
}
