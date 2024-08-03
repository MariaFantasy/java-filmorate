package ru.yandex.practicum.filmorate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class FilmorateApp {
    private final static Logger log = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(FilmorateApp.class);

    public static void main(String[] args) {
        log.setLevel(Level.DEBUG);
        log.info("Start working");
        SpringApplication.run(FilmorateApp.class, args);
    }
}
