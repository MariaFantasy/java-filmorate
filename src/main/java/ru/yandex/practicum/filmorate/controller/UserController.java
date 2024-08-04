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
        log.info("Пришел GET запрос /users");
        log.info("Отправлен ответ GET /users с телом: " + users.values());
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Пришел POST запрос /users с телом: " + user);
        validate(user);
        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Поле Name пустое, оно будет заполнено полем Login.");
            user.setName(user.getLogin());
        }
        final long userId = getNextId();
        user.setId(userId);
        users.put(userId, user);
        log.info("Отправлен ответ POST /users с телом: " + user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Пришел PUT запрос /users с телом: " + user);
        final Long userId = user.getId();
        if (userId == null) {
            log.info("Запрос PUT /users обработан не был по причине: Id должен быть указан");
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (users.containsKey(userId)) {
            validate(user);
            if (user.getName() == null || user.getName().isBlank()) {
                log.debug("Поле Name пустое, оно будет заполнено полем Login.");
                user.setName(user.getLogin());
            }
            users.put(userId, user);
            log.info("Отправлен ответ PUT /users с телом: " + user);
            return user;
        }
        log.info("Запрос PUT /users обработан не был по причине: Фильм с id = " + userId + " не найден");
        throw new NotFoundException("Пользователь с id = " + userId + " не найден");
    }

    private long getNextId() {
        return ++userCounter;
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
