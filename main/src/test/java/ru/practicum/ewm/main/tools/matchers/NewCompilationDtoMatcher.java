package ru.practicum.ewm.main.tools.matchers;

import org.mockito.ArgumentMatcher;
import ru.practicum.ewm.main.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.main.tools.factories.CompilationFactory;

public class NewCompilationDtoMatcher implements ArgumentMatcher<NewCompilationDto> {
    private final NewCompilationDto dto;

    private NewCompilationDtoMatcher(NewCompilationDto dto) {
        this.dto = dto;
    }

    public static NewCompilationDtoMatcher equalTo(NewCompilationDto dto) {
        return new NewCompilationDtoMatcher(dto);
    }

    @Override
    public boolean matches(NewCompilationDto dto) {
        return CompilationFactory.equals(this.dto, dto);
    }
}