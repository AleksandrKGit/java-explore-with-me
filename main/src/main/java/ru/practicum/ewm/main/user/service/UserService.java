package ru.practicum.ewm.main.user.service;

import org.springframework.lang.Nullable;
import ru.practicum.ewm.main.user.dto.NewUserRequest;
import ru.practicum.ewm.main.user.dto.UserDto;
import java.util.List;

public interface UserService {
    UserDto create(NewUserRequest dto);

    List<UserDto> find(@Nullable List<Long> ids, Integer from, Integer size);

    void delete(Long id);
}
