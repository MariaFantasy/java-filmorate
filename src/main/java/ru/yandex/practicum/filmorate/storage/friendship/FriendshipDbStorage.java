package ru.yandex.practicum.filmorate.storage.friendship;


import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friendship.mapper.FriendshipRowMapper;

import java.util.Collection;

@Repository("friendshipDbStorage")
@RequiredArgsConstructor
public class FriendshipDbStorage implements FriendshipStorage {
    private static final String ADD_FRIEND_QUERY = "MERGE INTO user_friend (user_id, friend_id, friendship_status_id) VALUES (?, ?, 2)";
    private static final String CONFIRM_FRIEND_QUERY = "UPDATE user_friend SET friendship_status_id = 1 WHERE user_id = ? AND friend_id = ?";
    private static final String DELETE_FRIEND_QUERY = "DELETE FROM user_friend WHERE user_id = ? AND friend_id = ?";
    private static final String GET_FRIENDS_QUERY = "SELECT friend_id FROM user_friend WHERE user_id = ? AND friendship_status_id = 1";

    private final JdbcTemplate jdbc;
    private final FriendshipRowMapper mapper;

    @Override
    public Collection<Long> getFriends(User user) {
        return jdbc.query(GET_FRIENDS_QUERY, mapper, user.getId());
    }

    @Override
    public void addFriend(User user, User newFriend) {
        jdbc.update(ADD_FRIEND_QUERY,
                user.getId(),
                newFriend.getId());

        jdbc.update(CONFIRM_FRIEND_QUERY,
                user.getId(),
                newFriend.getId());

        jdbc.update(ADD_FRIEND_QUERY,
                newFriend.getId(),
                user.getId());
    }

    @Override
    public void confirmFriend(User user, User newFriend) {
        jdbc.update(CONFIRM_FRIEND_QUERY, user.getId(), newFriend.getId());
    }

    @Override
    public void deleteFriend(User user, User oldFriend) {
        jdbc.update(DELETE_FRIEND_QUERY, user.getId(), oldFriend.getId());
    }
}
