package ru.practicum.ewm.main.compilation.service;

import ru.practicum.ewm.main.compilation.dto.CompilationDto;
import ru.practicum.ewm.main.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.main.compilation.dto.UpdateCompilationRequest;
import java.util.List;

public interface CompilationService {
    CompilationDto create(NewCompilationDto dto);

    CompilationDto get(Long id);

    List<CompilationDto> find(Boolean pinned, Integer from, Integer size);

    CompilationDto update(Long id, UpdateCompilationRequest dto);

    void delete(Long id);
}
