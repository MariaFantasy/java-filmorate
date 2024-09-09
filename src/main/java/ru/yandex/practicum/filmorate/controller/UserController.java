package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserStorage userStorage;
    private final UserService userService;

    @GetMapping
    public Collection<User> findAll() {
        log.info("Пришел GET запрос /users");
        Collection<User> allUsers = userStorage.findAll();
        log.info("Отправлен ответ GET /users с телом: {}", allUsers);
        return allUsers;
    }

    @GetMapping("/{userId}")
    public User findById(@PathVariable Long userId) {
        log.info("Пришел GET запрос /users/{}", userId);
        final User user = userStorage.findById(userId);
        if (user == null) {
            log.info("Запрос GET /users/{} обработан не был по причине: Пользователь с id = {} не найден", userId, userId);
            throw new NotFoundException("Пользователь с id = " + userId + " не найден.");
        }
        log.info("Отправлен ответ GET /users/{} с телом: {}", userId, user);
        return user;
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Пришел POST запрос /users с телом: {}", user);
        validate(user);
        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Поле Name пустое, оно будет заполнено полем Login.");
            user.setName(user.getLogin());
        }
        userStorage.create(user);
        log.info("Отправлен ответ POST /users с телом: {}", user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Пришел PUT запрос /users с телом: {}", user);
        final Long userId = user.getId();
        if (userId == null) {
            log.info("Запрос PUT /users обработан не был по причине: Id должен быть указан");
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (userStorage.findById(userId) != null) {
            validate(user);
            if (user.getName() == null || user.getName().isBlank()) {
                log.debug("Поле Name пустое, оно будет заполнено полем Login.");
                user.setName(user.getLogin());
            }
            userStorage.update(user);
            log.info("Отправлен ответ PUT /users с телом: {}", user);
            return user;
        }
        log.info("Запрос PUT /users обработан не был по причине: Пользователь с id = {} не найден", userId);
        throw new NotFoundException("Пользователь с id = " + userId + " не найден");
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public User addFriendToUser(@PathVariable Long userId, @PathVariable Long friendId) {
        log.info("Пришел PUT запрос /users/{}/friends/{}", userId, friendId);
        final User user = userStorage.findById(userId);
        final User friend = userStorage.findById(friendId);
        if (user == null) {
            log.info("Запрос PUT /users/{}/friends/{} обработан не был по причине: Пользователь с id = {} не найден", userId, friendId, userId);
            throw new NotFoundException("Пользователь с id = " + userId + " не найден.");
        }
        if (friend == null) {
            log.info("Запрос PUT /users/{}/friends/{} обработан не был по причине: Пользователь с id = {} не найден", userId, friendId, friendId);
            throw new NotFoundException("Пользователь с id = " + friendId + " не найден.");
        }
        userService.addFiend(user, friend);
        log.info("Отправлен ответ PUT /users/{}/friends/{} с телом: {}", userId, friendId, user);
        return user;
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public User deleteFriendInUser(@PathVariable Long userId, @PathVariable Long friendId) {
        log.info("Пришел DELETE запрос /users/{}/friends/{}", userId, friendId);
        final User user = userStorage.findById(userId);
        final User friend = userStorage.findById(friendId);
        if (user == null) {
            log.info("Запрос DELETE /users/{}/friends/{} обработан не был по причине: Пользователь с id = {} не найден", userId, friendId, userId);
            throw new NotFoundException("Пользователь с id = " + userId + " не найден.");
        }
        if (friend == null) {
            log.info("Запрос DELETE /users/{}/friends/{} обработан не был по причине: Пользователь с id = {} не найден", userId, friendId, friendId);
            throw new NotFoundException("Пользователь с id = " + friendId + " не найден.");
        }
        userService.deleteFriend(user, friend);
        log.info("Отправлен ответ DELETE /users/{}/friends/{} с телом: {}", userId, friendId, user);
        return user;
    }

    @GetMapping("/{userId}/friends")
    public Collection<User> getUserFriends(@PathVariable Long userId) {
        log.info("Пришел GET запрос /users/{}/friends", userId);
        final User user = userStorage.findById(userId);
        if (user == null) {
            log.info("Запрос GET /users/{}/friends обработан не был по причине: Пользователь с id = {} не найден", userId, userId);
            throw new NotFoundException("Пользователь с id = " + userId + " не найден.");
        }
        Collection<User> friends = user.getFriends().stream()
                .map(userStorage::findById)
                .collect(Collectors.toCollection(HashSet::new));
        log.info("Отправлен ответ GET /users/{}/friends с телом: {}", userId, friends);
        return friends;
    }

    @GetMapping("/{userId}/friends/common/{otherId}")
    public Collection<User> getIntersectionOfFriends(@PathVariable Long userId, @PathVariable Long otherId) {
        log.info("Пришел GET запрос /users/{}/friends/common/{}", userId, otherId);
        final User user = userStorage.findById(userId);
        final User otherUser = userStorage.findById(otherId);
        if (user == null) {
            log.info("Запрос GET /users/{}/friends/common/{} обработан не был по причине: Пользователь с id = {} не найден", userId, otherId, userId);
            throw new NotFoundException("Пользователь с id = " + userId + " не найден.");
        }
        if (otherUser == null) {
            log.info("Запрос GET /users/{}/friends/common/{} обработан не был по причине: Пользователь с id = {} не найден", userId, otherId, otherId);
            throw new NotFoundException("Пользователь с id = " + otherId + " не найден.");
        }
        Collection<User> intersectionOfFriends = userService.getIntersectionOfFriends(user, otherUser);
        log.info("Отправлен ответ GET /users/{}/friends/common/{} с телом: {}", userId, otherId, intersectionOfFriends);
        return intersectionOfFriends;
    }

    private void validate(final User user) {
        if (user.getLogin().contains(" ")) {
            log.debug("Пользователь не прошел валидацию по причине: Логин не может содержать пробелы");
            throw new ConditionsNotMetException("Логин не может содержать пробелы");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.debug("Пользователь не прошел валидацию по причине: ата рождения не может быть в будущем");
            throw new ConditionsNotMetException("Дата рождения не может быть в будущем.");
        }
    }
}
