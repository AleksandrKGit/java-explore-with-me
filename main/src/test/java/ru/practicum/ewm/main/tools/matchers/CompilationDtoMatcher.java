package ru.practicum.ewm.main.tools.matchers;

import org.mockito.ArgumentMatcher;
import ru.practicum.ewm.main.compilation.dto.CompilationDto;
import ru.practicum.ewm.main.tools.factories.CompilationFactory;

public class CompilationDtoMatcher implements ArgumentMatcher<CompilationDto> {
    private final CompilationDto dto;

    private CompilationDtoMatcher(CompilationDto dto) {
        this.dto = dto;
    }

    public static CompilationDtoMatcher equalTo(CompilationDto dto) {
        return new CompilationDtoMatcher(dto);
    }

    @Override
    public boolean matches(CompilationDto dto) {
        return CompilationFactory.equals(this.dto, dto);
    }
}