package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FriendshipService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.friendship.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.friendship.InMemoryFriendshipStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {
    public static UserStorage userStorage = new InMemoryUserStorage();
    public static FilmStorage filmStorage = new InMemoryFilmStorage();
    public static FriendshipStorage friendshipStorage = new InMemoryFriendshipStorage();
    public static FriendshipService friendshipService = new FriendshipService(friendshipStorage);
    public static UserService userService = new UserService(userStorage, friendshipService, filmStorage);
    public static UserController userController = new UserController(userService);
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
        User user = new User(1L, null, "login", "name", LocalDate.of(2024, 1, 1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Validation errors expected");
    }

    @Test
    public void testUserCreatedIfAllFieldsNormal() {
        User user = new User(1L, "myemail@gmail.com", "login", "name", LocalDate.of(2024, 1, 1));
        User createdUser = userController.create(user);
        assertEquals(user.getName(), createdUser.getName(), "Expected equals users");
    }

    @Test
    public void testAddFriendNormal() {
        User user = new User(1L, "myemail@gmail.com", "login", "name", LocalDate.of(2024, 1, 1));
        User friend = new User(1L, "myemail2@gmail.com", "login2", "name2", LocalDate.of(2024, 1, 1));
        User createdUser = userController.create(user);
        User createdFriend = userController.create(friend);
        userController.addFriendToUser(createdUser.getId(), createdFriend.getId());
        assertTrue(createdUser.getAcceptedFriends().contains(createdFriend.getId()));
        assertTrue(createdFriend.getAcceptedFriends().contains(createdUser.getId()));
    }

    @Test
    public void testAddFriendWhenNotFoundUser() {
        User user = new User(10000L, "myemail@gmail.com", "login", "name", LocalDate.of(2024, 1, 1));
        User friend = new User(100000L, "myemail2@gmail.com", "login2", "name2", LocalDate.of(2024, 1, 1));
        User createdFriend = userController.create(friend);
        assertThrows(NotFoundException.class, () -> userController.addFriendToUser(user.getId(), createdFriend.getId()));
    }

    @Test
    public void testAddFriendWhenNotFoundFriend() {
        User user = new User(10000L, "myemail@gmail.com", "login", "name", LocalDate.of(2024, 1, 1));
        User friend = new User(100000L, "myemail2@gmail.com", "login2", "name2", LocalDate.of(2024, 1, 1));
        User createdUser = userController.create(user);
        assertThrows(NotFoundException.class, () -> userController.addFriendToUser(createdUser.getId(), friend.getId()));
    }

    @Test
    public void testDeleteFriendNormal() {
        User user = new User(1L, "myemail@gmail.com", "login", "name", LocalDate.of(2024, 1, 1));
        User friend = new User(1L, "myemail2@gmail.com", "login2", "name2", LocalDate.of(2024, 1, 1));
        User createdUser = userController.create(user);
        User createdFriend = userController.create(friend);
        userController.addFriendToUser(createdUser.getId(), createdFriend.getId());
        userController.deleteFriendInUser(createdUser.getId(), createdFriend.getId());
        assertFalse(createdUser.getAcceptedFriends().contains(createdFriend.getId()));
        assertFalse(createdFriend.getAcceptedFriends().contains(createdUser.getId()));
    }

    @Test
    public void testDeleteFriendWhenNotFoundUser() {
        User user = new User(10000L, "myemail@gmail.com", "login", "name", LocalDate.of(2024, 1, 1));
        User friend = new User(1L, "myemail2@gmail.com", "login2", "name2", LocalDate.of(2024, 1, 1));
        User createdFriend = userController.create(friend);
        assertThrows(NotFoundException.class, () -> userController.deleteFriendInUser(user.getId(), createdFriend.getId()));
    }

    @Test
    public void testDeleteFriendWhenNotFoundFriend() {
        User user = new User(10000L, "myemail@gmail.com", "login", "name", LocalDate.of(2024, 1, 1));
        User friend = new User(1L, "myemail2@gmail.com", "login2", "name2", LocalDate.of(2024, 1, 1));
        User createdFriend = userController.create(friend);
        assertThrows(NotFoundException.class, () -> userController.deleteFriendInUser(user.getId(), createdFriend.getId()));
    }

    @Test
    public void testDeleteFriendWhenNotFoundFriendsInUser() {
        User user = new User(1L, "myemail@gmail.com", "login", "name", LocalDate.of(2024, 1, 1));
        User friend = new User(1L, "myemail2@gmail.com", "login2", "name2", LocalDate.of(2024, 1, 1));
        User createdUser = userController.create(user);
        User createdFriend = userController.create(friend);
        userController.addFriendToUser(createdUser.getId(), createdFriend.getId());
        userController.deleteFriendInUser(createdUser.getId(), createdFriend.getId());
        assertEquals(0, createdUser.getAcceptedFriends().size());
    }

    @Test
    public void testGetListOfFriends() {
        User user = new User(1L, "myemail@gmail.com", "login", "name", LocalDate.of(2024, 1, 1));
        User friend = new User(1L, "myemail2@gmail.com", "login2", "name2", LocalDate.of(2024, 1, 1));
        User createdUser = userController.create(user);
        User createdFriend = userController.create(friend);
        userController.addFriendToUser(createdUser.getId(), createdFriend.getId());
        assertEquals(1, createdUser.getAcceptedFriends().size());
        assertTrue(createdUser.getAcceptedFriends().contains(createdFriend.getId()));
    }

    @Test
    public void testGetIntersectionListOfFriends() {
        User user1 = new User(1L, "myemail@gmail.com", "login", "name", LocalDate.of(2024, 1, 1));
        User user2 = new User(1L, "myemail2@gmail.com", "login2", "name2", LocalDate.of(2024, 1, 1));
        User user3 = new User(1L, "myemail2@gmail.com", "login2", "name2", LocalDate.of(2024, 1, 1));
        User user4 = new User(1L, "myemail2@gmail.com", "login2", "name2", LocalDate.of(2024, 1, 1));
        User user5 = new User(1L, "myemail2@gmail.com", "login2", "name2", LocalDate.of(2024, 1, 1));
        User createdUser1 = userController.create(user1);
        User createdUser2 = userController.create(user2);
        User createdUser3 = userController.create(user3);
        User createdUser4 = userController.create(user4);
        User createdUser5 = userController.create(user5);
        userController.addFriendToUser(createdUser1.getId(), createdUser3.getId());
        userController.addFriendToUser(createdUser1.getId(), createdUser4.getId());
        userController.addFriendToUser(createdUser2.getId(), createdUser3.getId());
        userController.addFriendToUser(createdUser2.getId(), createdUser5.getId());
        assertEquals(1, userController.getIntersectionOfFriends(createdUser1.getId(), createdUser2.getId()).size());
        assertTrue(userController.getIntersectionOfFriends(createdUser1.getId(), createdUser2.getId()).contains(createdUser3));
    }
}
