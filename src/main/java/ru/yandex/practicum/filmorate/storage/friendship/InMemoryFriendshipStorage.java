package ru.yandex.practicum.filmorate.storage.friendship;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

@Component("inMemoryFriendshipStorage")
public class InMemoryFriendshipStorage implements FriendshipStorage {

    @Override
    public Collection<Long> getFriends(User user) {
        return user.getAcceptedFriends();
    }

    @Override
    public void addFriend(User user, User newFriend) {
        user.getAcceptedFriends().add(newFriend.getId());
        newFriend.getAcceptedFriends().add(user.getId());
    }

    @Override
    public void confirmFriend(User user, User newFriend) {
    }

    @Override
    public void deleteFriend(User user, User oldFriend) {
        user.getAcceptedFriends().remove(oldFriend.getId());
        oldFriend.getAcceptedFriends().remove(user.getId());
    }
}
