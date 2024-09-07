package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long userCounter = 0;

    @Override
    public User create(User user) {
        final long userId = getNextId();
        user.setId(userId);
        users.put(userId, user);
        return user;
    }

    @Override
    public User update(User user) {
        final Long userId = user.getId();
        users.put(userId, user);
        return user;
    }

    @Override
    public User delete(User user) {
        final Long userId = user.getId();
        users.remove(userId);
        return user;
    }

    @Override
    public User findById(Long id) {
        return users.get(id);
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    private long getNextId() {
        return ++userCounter;
    }
}
