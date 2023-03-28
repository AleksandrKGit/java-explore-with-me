package ru.practicum.ewm.main.tools.matchers;

import org.mockito.ArgumentMatcher;
import ru.practicum.ewm.main.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.main.tools.factories.EventFactory;

public class UpdateEventAdminRequestMatcher implements ArgumentMatcher<UpdateEventAdminRequest> {
    private final UpdateEventAdminRequest dto;

    private UpdateEventAdminRequestMatcher(UpdateEventAdminRequest dto) {
        this.dto = dto;
    }

    public static UpdateEventAdminRequestMatcher equalTo(UpdateEventAdminRequest dto) {
        return new UpdateEventAdminRequestMatcher(dto);
    }

    @Override
    public boolean matches(UpdateEventAdminRequest dto) {
        return EventFactory.equals(this.dto, dto);
    }
}