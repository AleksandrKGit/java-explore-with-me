package ru.practicum.ewm.main.tools.factories;

import ru.practicum.ewm.main.event.dto.UserShortDto;
import ru.practicum.ewm.main.user.User;
import ru.practicum.ewm.main.user.dto.NewUserRequest;
import ru.practicum.ewm.main.user.dto.UserDto;
import java.util.Objects;

public class UserFactory {

    public static User createUser(Long id, String name, String email) {
        User user = new User();

        user.setId(id);
        user.setName(name);
        user.setEmail(email);

        return user;
    }

    public static UserDto createUserDto(Long id, String name, String email) {
        UserDto dto = new UserDto();

        dto.setId(id);
        dto.setName(name);
        dto.setEmail(email);

        return dto;
    }

    public static UserShortDto createUserShortDto(Long id, String name) {
        UserShortDto dto = new UserShortDto();

        dto.setId(id);
        dto.setName(name);

        return dto;
    }

    public static NewUserRequest createNewUserRequest(String name, String email) {
        NewUserRequest dto = new NewUserRequest();

        dto.setName(name);
        dto.setEmail(email);

        return dto;
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

    public static boolean equals(User user1, User user2) {
        if (user1 == null && user2 == null) {
            return true;
        }

        return user1 != null && user2 != null
                && Objects.equals(user1.getId(), user2.getId())
                && Objects.equals(user1.getName(), user2.getName())
                && Objects.equals(user1.getEmail(), user2.getEmail());
    }

    public static boolean equals(UserDto dto1, UserDto dto2) {
        if (dto1 == null && dto2 == null) {
            return true;
        }

        return dto1 != null && dto2 != null
                && Objects.equals(dto1.getId(), dto2.getId())
                && Objects.equals(dto1.getName(), dto2.getName())
                && Objects.equals(dto1.getEmail(), dto2.getEmail());
    }

    public static boolean equals(UserShortDto dto1, UserShortDto dto2) {
        if (dto1 == null && dto2 == null) {
            return true;
        }

        return dto1 != null && dto2 != null
                && Objects.equals(dto1.getId(), dto2.getId())
                && Objects.equals(dto1.getName(), dto2.getName());
    }

    public static boolean equals(NewUserRequest dto1, NewUserRequest dto2) {
        if (dto1 == null && dto2 == null) {
            return true;
        }

        return dto1 != null && dto2 != null
                && Objects.equals(dto1.getName(), dto2.getName())
                && Objects.equals(dto1.getEmail(), dto2.getEmail());
    }
}