package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friendship.FriendshipStorage;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FriendshipService {
    private final FriendshipStorage friendshipStorage;

    public FriendshipService(@Qualifier("friendshipDbStorage") FriendshipStorage friendshipStorage) {
        this.friendshipStorage = friendshipStorage;
    }

    public Collection<Long> getFriends(User user) {
        return friendshipStorage.getFriends(user);
    }

    public void addFiend(User user, User newFriend) {
        friendshipStorage.addFriend(user, newFriend);
    }

    public void confirmFriend(User user, User newFriend) {
        friendshipStorage.confirmFriend(user, newFriend);
    }

    public void deleteFriend(User user, User oldFriend) {
        friendshipStorage.deleteFriend(user, oldFriend);
    }

    public Set<Long> getIntersectionOfFriends(User user1, User user2) {
        final Collection<Long> user1Friends = getFriends(user1);
        final Collection<Long> user2Friends = getFriends(user2);

        return user1Friends.stream()
                .filter(user2Friends::contains)
                .collect(Collectors.toSet());
    }

    public boolean isUserFriendExists(Long userId) {
        return friendshipStorage.isUserFriendExists(userId);
    }
}
