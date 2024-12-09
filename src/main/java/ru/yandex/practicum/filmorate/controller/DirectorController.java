package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Collection;

@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {
    private static final Logger log = LoggerFactory.getLogger(DirectorController.class);
    private final DirectorService directorService;

    @GetMapping
    public Collection<Director> findAll() {
        log.info("Пришел GET запрос /directors");
        Collection<Director> allDirectors = directorService.findAll();
        log.info("Отправлен ответ GET /directors с телом: {}", allDirectors);
        return allDirectors;

    }

    @GetMapping("/{directorId}")
    private Director findById(@PathVariable Long directorId) {
        log.info("Пришел GET запрос /directors/{}", directorId);
        final Director director = directorService.findById(directorId);
        if (director == null) {
            log.info("Запрос GET /directors/{} обработан не был по причине: Режиссер с id = {} не найден", directorId, director);
            throw new NotFoundException("Режиссер с id = " + directorId + " не найден.");
        }
        log.info("Отправлен ответ GET /directors/{} с телом: {}", directorId, director);
        return director;
    }

    @PostMapping
    public Director create(@Valid @RequestBody Director director) {
        log.info("Пришел POST запрос /directors с телом: {}", director);
        Director createdDirector = directorService.create(director);
        log.info("Отправлен ответ POST /directors с телом: {}", createdDirector);
        return createdDirector;
    }

    @PutMapping
    public Director update(@Valid @RequestBody Director director) {
        log.info("Пришел PUT запрос /directors с телом: {}", director);
        final Long directorId = director.getId();
        if (directorId == null) {
            log.info("Запрос PUT /directors обработан не был по причине: Id должен быть указан");
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (directorService.findById(directorId) != null) {
            Director updatedDirector = directorService.update(director);
            log.info("Отправлен ответ PUT /directors с телом: {}", updatedDirector);
            return updatedDirector;
        }
        log.info("Запрос PUT /directors обработан не был по причине: Режиссер с id = {} не найден", directorId);
        throw new NotFoundException("Режиссер с id = " + directorId + " не найден");
    }

    @DeleteMapping("/{directorId}")
    private Director deleteById(@PathVariable Long directorId) {
        log.info("Пришел DELETE запрос /directors/{}", directorId);
        final Director director = directorService.findById(directorId);
        directorService.delete(director);
        log.info("Отправлен ответ DELETE /directors/{} с телом: {}", directorId, director);
        return director;
    }
}
