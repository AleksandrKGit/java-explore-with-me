package ru.practicum.ewm.main.tools.matchers;

import org.mockito.ArgumentMatcher;
import ru.practicum.ewm.main.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.main.tools.factories.EventFactory;

public class UpdateEventUserRequestMatcher implements ArgumentMatcher<UpdateEventUserRequest> {
    private final UpdateEventUserRequest dto;

    private UpdateEventUserRequestMatcher(UpdateEventUserRequest dto) {
        this.dto = dto;
    }

    public static UpdateEventUserRequestMatcher equalTo(UpdateEventUserRequest dto) {
        return new UpdateEventUserRequestMatcher(dto);
    }

    @Override
    public boolean matches(UpdateEventUserRequest dto) {
        return EventFactory.equals(this.dto, dto);
    }
}