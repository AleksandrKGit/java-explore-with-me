package ru.practicum.ewm.main.tools.matchers;

import org.mockito.ArgumentMatcher;
import ru.practicum.ewm.main.event.dto.UserShortDto;
import ru.practicum.ewm.main.tools.factories.UserFactory;

public class UserShortDtoMatcher implements ArgumentMatcher<UserShortDto> {
    private final UserShortDto dto;

    private UserShortDtoMatcher(UserShortDto dto) {
        this.dto = dto;
    }

    public static UserShortDtoMatcher equalTo(UserShortDto dto) {
        return new UserShortDtoMatcher(dto);
    }

    @Override
    public boolean matches(UserShortDto dto) {
        return UserFactory.equals(this.dto, dto);
    }
}