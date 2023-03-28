package ru.practicum.ewm.main.tools.matchers;

import org.mockito.ArgumentMatcher;
import ru.practicum.ewm.main.event.dto.EventFullDto;
import ru.practicum.ewm.main.tools.factories.EventFactory;

public class EventFullDtoMatcher implements ArgumentMatcher<EventFullDto> {
    private final EventFullDto dto;

    private EventFullDtoMatcher(EventFullDto dto) {
        this.dto = dto;
    }

    public static EventFullDtoMatcher equalTo(EventFullDto dto) {
        return new EventFullDtoMatcher(dto);
    }

    @Override
    public boolean matches(EventFullDto dto) {
        return EventFactory.equals(this.dto, dto);
    }
}