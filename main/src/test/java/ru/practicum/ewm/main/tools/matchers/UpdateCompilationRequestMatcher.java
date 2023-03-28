package ru.practicum.ewm.main.tools.matchers;

import org.mockito.ArgumentMatcher;
import ru.practicum.ewm.main.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.main.tools.factories.CompilationFactory;

public class UpdateCompilationRequestMatcher implements ArgumentMatcher<UpdateCompilationRequest> {
    private final UpdateCompilationRequest dto;

    private UpdateCompilationRequestMatcher(UpdateCompilationRequest dto) {
        this.dto = dto;
    }

    public static UpdateCompilationRequestMatcher equalTo(UpdateCompilationRequest dto) {
        return new UpdateCompilationRequestMatcher(dto);
    }

    @Override
    public boolean matches(UpdateCompilationRequest dto) {
        return CompilationFactory.equals(this.dto, dto);
    }
}