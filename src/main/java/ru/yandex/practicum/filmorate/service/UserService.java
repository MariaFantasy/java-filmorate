package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private UserStorage userStorage;

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
