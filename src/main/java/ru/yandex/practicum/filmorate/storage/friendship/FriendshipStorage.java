package ru.yandex.practicum.filmorate.storage.friendship;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface FriendshipStorage {

    public Collection<Long> getFriends(User user);

    public void addFriend(User user, User newFriend);

    public void confirmFriend(User user, User newFriend);

    public void deleteFriend(User user, User oldFriend);

    default boolean isUserFriendExists(Long id) {
        return false;
    }
}