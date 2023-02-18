package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;
    private final UserValidator userValidator;

    public List<User> getAll() {
        return userStorage.getAll();
    }

    public User getById(long id) {
        return userStorage.getById(id);
    }

    public User add(User user) {
        userValidator.validate(user);
        checkName(user);
        return userStorage.add(user);
    }

    public User update(User user) {
        userValidator.validate(user);
        checkName(user);
        return userStorage.update(user);
    }

    public void addFriends(long id, long friendId) {
        User user = userStorage.getById(id);
        User friend = userStorage.getById(friendId);
        user.addFriend(friendId);
        friend.addFriend(id);
        log.info("Users {} and {} now friends!" , user.getName(), friend.getName());
    }

    public void removeFriends(long id, long friendId) {
        User user = userStorage.getById(id);
        User friend = userStorage.getById(friendId);
        user.removeFriend(friendId);
        friend.removeFriend(id);
        log.info("Users {} and {} stopped being friends!" , user.getName(), friend.getName());
    }

    public List<User> getAllFriends(long id) {
        List<User> friends = new ArrayList<>();
        List<Long> friendsIds = userStorage.getById(id).getFriends().stream()
                .distinct()
                .collect(Collectors.toList());
        for (long friendId : friendsIds) {
            User friend = userStorage.getById(friendId);
            friends.add(friend);
        }
        return friends;
    }

    public List<User> getCommonFriends(long id, long otherId) {
        List<User> first = getAllFriends(id);
        List<User> second = getAllFriends(otherId);
        return first.stream()
                .filter(second::contains)
                .collect(Collectors.toList());
    }

    private void checkName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Username is empty, login is used as the name");
            user.setName(user.getLogin());
        }
    }
}
