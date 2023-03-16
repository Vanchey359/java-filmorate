package ru.yandex.practicum.filmorate.storage.friends;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

@Component
@Slf4j
public class FriendsStorage {
    private final JdbcTemplate jdbcTemplate;

    public FriendsStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    public void addFriend (Long id, Long friendId) {
        if (isUserExists(id) && isUserExists(friendId)) {
            String sql = "INSERT INTO friends_status (user_id, friend_id, approved) VALUES (?, ?, ?)";
            jdbcTemplate.update(sql, id, friendId, false);
            log.info("user {} add user {} to friends", id, friendId);
        } else {
            throw new NotFoundException("Attempt to add unknown user to friends");
        }
    }

    public Collection<User> findFriends(Long id) {
        String sql = "SELECT friend_id, email, login, name, birthday FROM friends_status JOIN users U " +
                "on friends_status.friend_id = u.user_id WHERE " +
                "friends_status.user_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new User(
                        rs.getLong("friend_id"),
                        rs.getString("email"),
                        rs.getString("login"),
                        rs.getString("name"),
                        rs.getDate("birthday").toLocalDate()),
                id
        );
    }

    private boolean isUserExists(Long id) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sql, id);
        return userRows.next();
    }

    public void deleteFriend(Long id, Long friendId) {
        if (isUserExists(id) && isUserExists(friendId)) {
            String sql = "DELETE FROM friends_status WHERE user_id = ? AND friend_id = ?";
            jdbcTemplate.update(sql, id, friendId);
        }  else {
            throw new NotFoundException("Attempt to delete unknown user to friends");
        }
    }
}
