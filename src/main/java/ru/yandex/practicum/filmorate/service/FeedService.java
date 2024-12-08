package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.types.EventType;
import ru.yandex.practicum.filmorate.model.types.Operation;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;

import java.time.Instant;
import java.util.Collection;

@Service
public class FeedService {
    private final FeedStorage feedStorage;

    public FeedService(@Qualifier("feedDbStorage") FeedStorage feedStorage) {
        this.feedStorage = feedStorage;
    }

    public Event create(Long user_id, Long entity_id, EventType eventType, Operation operation) {
        Event event = new Event();
        event.setUserId(user_id);
        event.setEntityId(entity_id);
        event.setEventType(eventType);
        event.setOperation(operation);
        event.setTimestamp(Instant.now().toEpochMilli());

        feedStorage.create(event);

        return event;
    }

    public Collection<Event> getUserFeed(User user) {
        return feedStorage.getUserFeed(user);
    }
}
