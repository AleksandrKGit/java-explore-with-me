package ru.practicum.ewm.main.tools.factories;

import ru.practicum.ewm.main.user.User;
import ru.practicum.ewm.main.user.dto.NewUserRequest;
import ru.practicum.ewm.main.user.dto.UserDto;

public class UserFactory {
    public static NewUserRequest createNewUserRequest(String name, String email) {
        NewUserRequest dto = new NewUserRequest();
        dto.setName(name);
        dto.setEmail(email);
        return dto;
    }

    public static UserDto createUserDto(Long id, String name, String email) {
        UserDto dto = new UserDto();
        dto.setId(id);
        dto.setName(name);
        dto.setEmail(email);
        return dto;
    }

    public static User createUser(Long id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    public static User copyOf(User user) {
        if (user == null) {
            return null;
        }

        User copy = new User();
        copy.setId(user.getId());
        copy.setName(user.getName());
        copy.setEmail(user.getEmail());

        return copy;
    }
}