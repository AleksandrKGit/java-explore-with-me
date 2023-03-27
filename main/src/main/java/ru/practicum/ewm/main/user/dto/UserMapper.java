package ru.practicum.ewm.main.user.dto;

import org.mapstruct.*;
import ru.practicum.ewm.main.user.User;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    User toEntity(NewUserRequest dto);

    UserDto toDto(User user);

    List<UserDto> toDto(List<User> users);
}