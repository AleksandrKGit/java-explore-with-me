package ru.practicum.ewm.main.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.ewm.main.user.User;
import java.util.List;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static ru.practicum.ewm.main.tools.factories.UserFactory.*;

@SpringBootTest(classes = {UserMapperImpl.class})
class UserMapperTest {
    @Autowired
    private UserMapper userMapper;

    @Test
    void toDto_withNotNullFields_shouldReturnDtoWithNotNullFields() {
        User source = createUser(10L, "userName", "user@email.com");

        UserDto target = userMapper.toDto(source);

        assertThat(target, allOf(
                hasProperty("id", equalTo(source.getId())),
                hasProperty("name", equalTo(source.getName())),
                hasProperty("email", equalTo(source.getEmail()))
        ));
    }

    @Test
    void toDto_withNull_shouldReturnNull() {
        assertThat(userMapper.toDto((User) null), is(nullValue()));
    }

    @Test
    void toDto_withNullFields_shouldReturnDtoWithNullFields() {
        User source = createUser(null, null, null);

        UserDto target = userMapper.toDto(source);

        assertThat(target, allOf(
                hasProperty("id", is(nullValue())),
                hasProperty("name", is(nullValue())),
                hasProperty("email", is(nullValue()))
        ));
    }

    @Test
    void toDtoList_withNotNull_shouldReturnUserDtoList() {
        User source = createUser(10L, "userName", "user@email.com");

        List<UserDto> target = userMapper.toDto(List.of(source));

        assertThat(target, contains(allOf(
                hasProperty("id", equalTo(source.getId())),
                hasProperty("name", equalTo(source.getName())),
                hasProperty("email", equalTo(source.getEmail()))
        )));
    }

    @Test
    void toDtoList_withNull_shouldReturnNull() {
        List<UserDto> target = userMapper.toDto((List<User>) null);

        assertThat(target, is(nullValue()));
    }

    @Test
    void toDtoList_withEmptyList_shouldReturnEmptyList() {
        List<UserDto> target = userMapper.toDto(List.of());

        assertThat(target, is(empty()));
    }

    @Test
    void toEntity_withNotNullFields_shouldReturnEntityWithNotNullFields() {
        NewUserRequest source = createNewUserRequest("userName", "user@email.com");

        User target = userMapper.toEntity(source);

        assertThat(target, allOf(
                hasProperty("id", is(nullValue())),
                hasProperty("name", equalTo(source.getName())),
                hasProperty("email", equalTo(source.getEmail()))
        ));
    }

    @Test
    void toEntity_withNull_shouldReturnNull() {
        assertThat(userMapper.toEntity(null), nullValue());
    }

    @Test
    void toEntity_withNullFields_shouldReturnEntityWithNullFields() {
        NewUserRequest source = createNewUserRequest(null, null);

        User target = userMapper.toEntity(source);

        assertThat(target, allOf(
                hasProperty("id", is(nullValue())),
                hasProperty("name", is(nullValue())),
                hasProperty("email", is(nullValue()))
        ));
    }
}