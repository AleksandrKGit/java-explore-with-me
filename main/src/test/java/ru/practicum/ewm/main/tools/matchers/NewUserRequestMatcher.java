package ru.practicum.ewm.main.tools.matchers;

import org.mockito.ArgumentMatcher;
import ru.practicum.ewm.main.user.dto.NewUserRequest;
import java.util.Objects;

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
        return dto != null && this.dto != null
                && Objects.equals(this.dto.getName(), dto.getName())
                && Objects.equals(this.dto.getEmail(), dto.getEmail());
    }
}
