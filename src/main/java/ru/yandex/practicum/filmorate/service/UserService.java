package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friends.FriendsStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;
    private final UserValidator userValidator;
    private final FriendsStorage friendsStorage;
    private Long id = 0L;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage, UserValidator userValidator, FriendsStorage friendsStorage) {
        this.userStorage = userStorage;
        this.userValidator = userValidator;
        this.friendsStorage = friendsStorage;
    }

    private Long generateId() {
        return id++;
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

    public Collection<User> getAll() {
        System.out.println(userStorage.getAll());
        return userStorage.getAll();
    }

    public User delete(User user) {
        return userStorage.delete(user);
    }

    public User getById(long id) {
        return userStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Request user with absent id"));
    }

    public void addFriend(Long id, Long friendId) {
        friendsStorage.addFriend(id, friendId);
        log.info("User id = {} added to friends user id={}", id, friendId);
    }

    public void deleteFriend(Long id, Long friendId) {
        friendsStorage.deleteFriend(id, friendId);
        log.info("User id = {} deleted from friends user id={}", id, friendId);
    }

    public Collection<User> findFriends(Long id) {
        return friendsStorage.findFriends(id);
    }

    public Collection<User> findSharedFriends(Long id, Long otherId) {
        return findFriends(id).stream()
                .filter(x -> findFriends(otherId).contains(x))
                .collect(Collectors.toList());
    }

    private void checkName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Username is empty, login is used as the name");
            user.setName(user.getLogin());
        }
    }
}
