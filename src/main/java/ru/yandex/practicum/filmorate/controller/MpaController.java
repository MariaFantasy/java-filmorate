package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {
    private static final Logger log = LoggerFactory.getLogger(MpaController.class);
    private final MpaService mpaService;

    @GetMapping
    public Collection<Mpa> findAll() {
        log.info("Пришел GET запрос /mpa");
        Collection<Mpa> allMpa = mpaService.findAll();
        log.info("Отправлен ответ GET /mpa с телом: {}", allMpa);
        return allMpa;
    }

    @GetMapping("/{mpaId}")
    public Mpa findById(@PathVariable Integer mpaId) {
        log.info("Пришел GET запрос /mpa/{}", mpaId);
        final Mpa mpa = mpaService.findById(mpaId);
        if (mpa == null) {
            log.info("Запрос GET /mpa/{} обработан не был по причине: Рейтинг с id = {} не найден", mpaId, mpaId);
            throw new NotFoundException("Рейтинг с id = " + mpaId + " не найден.");
        }
        log.info("Отправлен ответ GET /mpa/{} с телом: {}", mpaId, mpa);
        return mpa;
    }
}
