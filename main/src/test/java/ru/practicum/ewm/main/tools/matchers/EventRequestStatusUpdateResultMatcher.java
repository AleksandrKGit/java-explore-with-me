package ru.practicum.ewm.main.tools.matchers;

import org.mockito.ArgumentMatcher;
import ru.practicum.ewm.main.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.main.tools.factories.RequestFactory;

public class EventRequestStatusUpdateResultMatcher implements ArgumentMatcher<EventRequestStatusUpdateResult> {
    private final EventRequestStatusUpdateResult dto;

    private EventRequestStatusUpdateResultMatcher(EventRequestStatusUpdateResult dto) {
        this.dto = dto;
    }

    public static EventRequestStatusUpdateResultMatcher equalTo(EventRequestStatusUpdateResult dto) {
        return new EventRequestStatusUpdateResultMatcher(dto);
    }

    @Override
    public boolean matches(EventRequestStatusUpdateResult dto) {
        return RequestFactory.equals(this.dto, dto);
    }
}