package ru.yandex.practicum.filmorate.storage.feed;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component("inMemoryFeedStorage")
@RequiredArgsConstructor
public class InMemoryFeedStorage implements FeedStorage {
    private final Map<Long, Event> events = new HashMap<>();
    private long eventCounter = 0;

    @Override
    public Event create(Event event) {
        final long eventId = getNextId();
        event.setEventId(eventId);
        events.put(eventId, event);
        return event;
    }

    @Override
    public Collection<Event> getUserFeed(User user) {
        return events.values().stream()
                .filter(event -> Objects.equals(event.getUserId(), user.getId()))
                .collect(Collectors.toCollection(HashSet::new));
    }

    private long getNextId() {
        return ++eventCounter;
    }
}
