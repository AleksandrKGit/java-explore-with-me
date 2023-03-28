package ru.practicum.ewm.main.tools.matchers;

import org.mockito.ArgumentMatcher;
import ru.practicum.ewm.main.event.dto.NewEventDto;
import ru.practicum.ewm.main.tools.factories.EventFactory;

public class NewEventDtoMatcher implements ArgumentMatcher<NewEventDto> {
    private final NewEventDto dto;

    private NewEventDtoMatcher(NewEventDto dto) {
        this.dto = dto;
    }

    public static NewEventDtoMatcher equalTo(NewEventDto dto) {
        return new NewEventDtoMatcher(dto);
    }

    @Override
    public boolean matches(NewEventDto dto) {
        return EventFactory.equals(this.dto, dto);
    }
}