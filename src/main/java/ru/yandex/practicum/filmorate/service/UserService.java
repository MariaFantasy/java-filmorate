package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User findById(Long userId) {
        return userStorage.findById(userId);
    }

    public void create(User user) {
        userStorage.create(user);
    }

    public void update(User user) {
        userStorage.update(user);
    }

    public void addFiend(User user, User newFriend) {
        user.getFriends().add(newFriend.getId());
        newFriend.getFriends().add(user.getId());
    }

    public void deleteFriend(User user, User oldFriend) {
        user.getFriends().remove(oldFriend.getId());
        oldFriend.getFriends().remove(user.getId());
    }

    public Set<User> getIntersectionOfFriends(User user1, User user2) {
        final Set<Long> user1Friends = user1.getFriends();
        final Set<Long> user2Friends = user2.getFriends();
        return user1Friends.stream()
                .filter(user2Friends::contains)
                .map(userStorage::findById)
                .collect(Collectors.toSet());
    }
}
