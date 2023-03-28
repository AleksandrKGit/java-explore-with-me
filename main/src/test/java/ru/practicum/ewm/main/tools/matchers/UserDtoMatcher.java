package ru.practicum.ewm.main.tools.matchers;

import org.mockito.ArgumentMatcher;
import ru.practicum.ewm.main.tools.factories.UserFactory;
import ru.practicum.ewm.main.user.dto.UserDto;

public class UserDtoMatcher implements ArgumentMatcher<UserDto> {
    private final UserDto dto;

    private UserDtoMatcher(UserDto dto) {
        this.dto = dto;
    }

    public static UserDtoMatcher equalTo(UserDto dto) {
        return new UserDtoMatcher(dto);
    }

    @Override
    public boolean matches(UserDto dto) {
        return UserFactory.equals(this.dto, dto);
    }
}