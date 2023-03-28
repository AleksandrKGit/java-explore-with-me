package ru.practicum.ewm.main.tools.matchers;

import org.mockito.ArgumentMatcher;
import ru.practicum.ewm.main.event.dto.EventShortDto;
import ru.practicum.ewm.main.tools.factories.EventFactory;

public class EventShortDtoMatcher implements ArgumentMatcher<EventShortDto> {
    private final EventShortDto dto;

    private EventShortDtoMatcher(EventShortDto dto) {
        this.dto = dto;
    }

    public static EventShortDtoMatcher equalTo(EventShortDto dto) {
        return new EventShortDtoMatcher(dto);
    }

    @Override
    public boolean matches(EventShortDto dto) {
        return EventFactory.equals(this.dto, dto);
    }
}