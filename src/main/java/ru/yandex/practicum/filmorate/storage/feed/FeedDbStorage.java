package ru.yandex.practicum.filmorate.storage.feed;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.feed.mapper.EventRowMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;

@Repository("feedDbStorage")
@RequiredArgsConstructor
public class FeedDbStorage implements FeedStorage {
    private final JdbcTemplate jdbc;
    private final EventRowMapper mapper;

    public static final String INSERT_QUERY = "INSERT INTO event(user_id, entity_id, event_type, operation, timestamp) VALUES (?, ?, ?, ?, ?)";
    private static final String GET_USER_FEED_QUERY = "SELECT event_id, user_id, entity_id, event_type, operation, timestamp FROM event WHERE user_id = ?";

    @Override
    public Event create(Event event) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, event.getUserId());
            ps.setObject(2, event.getEntityId());
            ps.setObject(3, event.getEventType().toString());
            ps.setObject(4, event.getOperation().toString());
            ps.setObject(5, event.getTimestamp());
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKeyAs(Long.class);
        event.setEventId(id);

        return event;
    }

    @Override
    public Collection<Event> getUserFeed(User user) {
        return jdbc.query(GET_USER_FEED_QUERY, mapper, user.getId());
    }
}
