package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.types.EventType;
import ru.yandex.practicum.filmorate.model.types.Operation;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;
    private final FriendshipService friendshipService;
    private final FeedService feedService;
    @Lazy
    @Autowired
    private FilmService filmService;

    public UserService(@Qualifier("userDbStorage") UserStorage userStorage,
                       FriendshipService friendshipService,
                       FeedService feedService) {
        this.userStorage = userStorage;
        this.friendshipService = friendshipService;
        this.feedService = feedService;
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
        feedService.create(user.getId(), newFriend.getId(), EventType.FRIEND, Operation.ADD);
    }

    public void confirmFriend(User user, User newFriend) {
        friendshipService.confirmFriend(user, newFriend);
    }

    public void deleteFriend(User user, User oldFriend) {
        friendshipService.deleteFriend(user, oldFriend);
        feedService.create(user.getId(), oldFriend.getId(), EventType.FRIEND, Operation.REMOVE);
    }

    public Set<User> getIntersectionOfFriends(User user1, User user2) {
        return friendshipService.getIntersectionOfFriends(user1, user2).stream()
                .map(this::findById)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }


    public List<Film> getRecommendationsByUserId(Long id) {
        userStorage.findById(id);

        return filmService.getRecommendationByUserId(id);
    }

    public void delete(User user) {
        userStorage.delete(user);
    }

    public Collection<Event> getUserFeed(Long userId) {
        final User user = findById(userId);
        return feedService.getUserFeed(user);
    }
}
