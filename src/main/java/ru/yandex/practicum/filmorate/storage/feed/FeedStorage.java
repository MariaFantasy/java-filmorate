package ru.yandex.practicum.filmorate.storage.feed;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface FeedStorage {

    public Event create(Event event);

    public Collection<Event> getUserFeed(User user);
}
