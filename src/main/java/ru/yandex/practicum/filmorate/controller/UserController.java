package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final Map<Long, User> users = new HashMap<>();
    private long userCounter = 0;

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        validate(user);
        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Name of User is empty");
            user.setName(user.getLogin());
        }
        final long userId = getNextId();
        user.setId(userId);
        users.put(userId, user);
        log.info("User with id " + userId + " created");
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        final Long userId = user.getId();
        if (userId == null) {
            log.debug("User has not been updated: Id is empty");
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (users.containsKey(userId)) {
            validate(user);
            if (user.getName() == null || user.getName().isBlank()) {
                log.debug("Name of User is empty");
                user.setName(user.getLogin());
            }
            users.put(userId, user);
            log.info("User with id " + userId  + " updated");
            return user;
        }
        log.info("User has not been updated: Id not found");
        throw new NotFoundException("Пользователь с id = " + userId + " не найден");
    }

    private long getNextId() {
        return ++userCounter;
    }

    private void validate(final User user) {
        if (user.getLogin().contains(" ")) {
            log.debug("User has not been created: Login contains spaces");
            throw new ConditionsNotMetException("Логин не может содержать пробелы");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.debug("User has not been created: Birthday is after now");
            throw new ConditionsNotMetException("Дата рождения не может быть в будущем.");
        }
    }
}
