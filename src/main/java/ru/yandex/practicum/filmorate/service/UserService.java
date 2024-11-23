package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;
    private final FriendshipService friendshipService;

    public UserService(@Qualifier("userDbStorage") UserStorage userStorage, FriendshipService friendshipService) {
        this.userStorage = userStorage;
        this.friendshipService = friendshipService;
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User findById(Long userId) {
        return userStorage.findById(userId);
    }

    public Collection<User> getFriends(User user) {
        return friendshipService.getFriends(user).stream()
                .map(this::findById)
                .collect(Collectors.toCollection(HashSet::new));
    }

    public void create(User user) {
        userStorage.create(user);
    }

    public void update(User user) {
        userStorage.update(user);
    }

    public void addFiend(User user, User newFriend) {
        friendshipService.addFiend(user, newFriend);
    }

    public void confirmFriend(User user, User newFriend) {
        friendshipService.confirmFriend(user, newFriend);
    }

    public void deleteFriend(User user, User oldFriend) {
        friendshipService.deleteFriend(user, oldFriend);
    }

    public Set<User> getIntersectionOfFriends(User user1, User user2) {
        return friendshipService.getIntersectionOfFriends(user1, user2).stream()
                .map(this::findById)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
