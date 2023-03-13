package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserStorageTests {

    private final UserDbStorage userDbStorage;

    @Test
    public void getByIdShouldGetUserById() {
        User user1 = new User(null, "ivanaverin@yandex.ru", "logIvan", "Ivan", LocalDate.of(1946, 8, 20));
        userDbStorage.add(user1);
        long id = user1.getId();

        Optional<User> userOptional = userDbStorage.getById(id);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", id)
                );
        userDbStorage.delete(user1);
    }

    @Test
    public void userUpdateShouldUpdateUserInDb() {
        User user1 = new User(null, "ivanaverin@yandex.ru", "logIvan", "Ivan", LocalDate.of(1946, 8, 20));
        userDbStorage.add(user1);
        long id = user1.getId();

        User userUpdate = new User(null, "ivanaverin@yandex.ru", "logUpdate", "Ivan", LocalDate.of(1946, 8, 20));
        userUpdate.setId(id);
        userDbStorage.update(userUpdate);

        Optional<User> userOptional = userDbStorage.getById(id);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(u -> assertThat(u)
                        .hasFieldOrPropertyWithValue("login", "logUpdate"));
    }

    @Test
    public void deleteShouldDeleteUserFromDb() {
        User user1 = new User(null, "ivanaverin@yandex.ru", "logIvan", "Ivan", LocalDate.of(1946, 8, 20));
        userDbStorage.add(user1);
        userDbStorage.delete(user1);

        Optional<User> userOptional = userDbStorage.getById(1L);
        assertThat(userOptional).isEmpty();
    }
}
