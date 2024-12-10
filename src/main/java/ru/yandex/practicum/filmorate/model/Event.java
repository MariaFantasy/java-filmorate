package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.yandex.practicum.filmorate.model.types.EventType;
import ru.yandex.practicum.filmorate.model.types.Operation;

@lombok.Data
@lombok.AllArgsConstructor
@lombok.RequiredArgsConstructor
@lombok.EqualsAndHashCode(of = {"eventId"})
public class Event {
    private Long eventId;

    @NotBlank
    private Long userId;

    private Long entityId;

    @NotBlank
    private EventType eventType;

    @NotBlank
    private Operation operation;

    @NotNull
    private Long timestamp;
}
