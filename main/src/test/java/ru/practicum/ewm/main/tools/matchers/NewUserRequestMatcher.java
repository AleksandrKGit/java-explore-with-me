package ru.practicum.ewm.main.tools.matchers;

import org.mockito.ArgumentMatcher;
import ru.practicum.ewm.main.tools.factories.UserFactory;
import ru.practicum.ewm.main.user.dto.NewUserRequest;

public class NewUserRequestMatcher implements ArgumentMatcher<NewUserRequest> {
    private final NewUserRequest dto;

    private NewUserRequestMatcher(NewUserRequest dto) {
        this.dto = dto;
    }

    public static NewUserRequestMatcher equalTo(NewUserRequest dto) {
        return new NewUserRequestMatcher(dto);
    }

    @Override
    public boolean matches(NewUserRequest dto) {
        return UserFactory.equals(this.dto, dto);
    }
}
