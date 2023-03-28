package ru.practicum.ewm.main.tools.matchers;

import org.mockito.ArgumentMatcher;
import ru.practicum.ewm.main.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.main.tools.factories.RequestFactory;

public class EventRequestStatusUpdateRequestMatcher implements ArgumentMatcher<EventRequestStatusUpdateRequest> {
    private final EventRequestStatusUpdateRequest dto;

    private EventRequestStatusUpdateRequestMatcher(EventRequestStatusUpdateRequest dto) {
        this.dto = dto;
    }

    public static EventRequestStatusUpdateRequestMatcher equalTo(EventRequestStatusUpdateRequest dto) {
        return new EventRequestStatusUpdateRequestMatcher(dto);
    }

    @Override
    public boolean matches(EventRequestStatusUpdateRequest dto) {
        return RequestFactory.equals(this.dto, dto);
    }
}