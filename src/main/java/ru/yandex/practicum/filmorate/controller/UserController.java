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

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.debug("User has not been created: Email is empty");
            throw new ConditionsNotMetException("Электронная почта не может быть пустой");
        }
        if (!user.getEmail().contains("@")) {
            log.debug("User has not been created: Email not contains @");
            throw new ConditionsNotMetException("Электронная почта должна содержать символ @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            log.debug("User has not been created: Login is empty");
            throw new ConditionsNotMetException("Логин не может быть пустым");
        }
        if (user.getLogin().contains(" ")) {
            log.debug("User has not been created: Login contains spaces");
            throw new ConditionsNotMetException("Логин не может содержать пробелы");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.debug("User has not been created: Birthday is after now");
            throw new ConditionsNotMetException("Дата рождения не может быть в будущем.");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Name of User is empty");
            user.setName(user.getLogin());
        } else {
            user.setName(user.getName());
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("User with id " + user.getId()  + " created");
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        if (newUser.getId() == null) {
            log.debug("User has not been updated: Id is empty");
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            if (newUser.getEmail() == null || newUser.getEmail().isBlank()) {
                log.debug("User has not been updated: Email is empty");
                throw new ConditionsNotMetException("Электронная почта не может быть пустой");
            }
            if (!newUser.getEmail().contains("@")) {
                log.debug("User has not been updated: Email not contains @");
                throw new ConditionsNotMetException("Электронная почта должна содержать символ @");
            }
            if (newUser.getLogin() == null || newUser.getLogin().isBlank()) {
                log.debug("User has not been updated: Login is empty");
                throw new ConditionsNotMetException("Логин не может быть пустым");
            }
            if (newUser.getLogin().contains(" ")) {
                log.debug("User has not been updated: Login contains spaces");
                throw new ConditionsNotMetException("Логин не может содержать пробелы");
            }
            if (newUser.getBirthday().isAfter(LocalDate.now())) {
                log.debug("User has not been updated: Birthday is after now");
                throw new ConditionsNotMetException("Дата рождения не может быть в будущем.");
            }
            oldUser.setEmail(newUser.getEmail());
            oldUser.setLogin(newUser.getLogin());
            if (newUser.getName() == null || newUser.getName().isBlank()) {
                log.debug("Name of User is empty");
                oldUser.setName(newUser.getLogin());
            } else {
                oldUser.setName(newUser.getName());
            }
            oldUser.setBirthday(newUser.getBirthday());
            log.info("User with id " + oldUser.getId()  + " updated");
            return oldUser;
        }
        log.info("User has not been updated: Id not found");
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
