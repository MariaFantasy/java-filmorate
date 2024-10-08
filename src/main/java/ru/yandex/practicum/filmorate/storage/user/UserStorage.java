package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    public User create(User user);

    public User update(User user);

    public User delete(User user);

    public User findById(Long id);

    public Collection<User> findAll();
}
