package ru.practicum.ewm.main.tools.matchers;

import org.mockito.ArgumentMatcher;
import ru.practicum.ewm.main.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.main.tools.factories.RequestFactory;

public class ParticipationRequestDtoMatcher implements ArgumentMatcher<ParticipationRequestDto> {
    private final ParticipationRequestDto dto;

    private ParticipationRequestDtoMatcher(ParticipationRequestDto dto) {
        this.dto = dto;
    }

    public static ParticipationRequestDtoMatcher equalTo(ParticipationRequestDto dto) {
        return new ParticipationRequestDtoMatcher(dto);
    }

    @Override
    public boolean matches(ParticipationRequestDto dto) {
        return RequestFactory.equals(this.dto, dto);
    }
}