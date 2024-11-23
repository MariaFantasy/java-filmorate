package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {
    private static final Logger log = LoggerFactory.getLogger(GenreController.class);
    private final GenreService genreService;

    @GetMapping
    public Collection<Genre> findAll() {
        log.info("Пришел GET запрос /genres");
        Collection<Genre> allGenres = genreService.findAll();
        log.info("Отправлен ответ GET /genres с телом: {}", allGenres);
        return allGenres;
    }

    @GetMapping("/{genreId}")
    public Genre findById(@PathVariable Integer genreId) {
        log.info("Пришел GET запрос /genres/{}", genreId);
        final Genre genre = genreService.findById(genreId);
        if (genre == null) {
            log.info("Запрос GET /genres/{} обработан не был по причине: Жанр c id = {} не найден", genreId, genreId);
            throw new NotFoundException("Жанр с id = " + genreId + " не найден.");
        }
        log.info("Отправлен ответ GET /genres/{} с телом: {}", genreId, genre);
        return genre;
    }
}
