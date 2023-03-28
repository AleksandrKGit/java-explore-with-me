package ru.practicum.ewm.main.compilation.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.main.compilation.CompilationRepository;
import ru.practicum.ewm.main.compilation.Compilation;
import ru.practicum.ewm.main.compilation.dto.CompilationDto;
import ru.practicum.ewm.main.compilation.dto.CompilationMapper;
import ru.practicum.ewm.main.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.main.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.main.event.repository.EventRepository;
import ru.practicum.ewm.main.event.model.Event;
import ru.practicum.ewm.main.exception.NotFoundException;
import ru.practicum.ewm.main.support.OffsetPageRequest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CompilationServiceImpl implements CompilationService {
    CompilationRepository repository;

    EventRepository eventRepository;

    CompilationMapper mapper;

    void mergeEvents(Compilation compilation, List<Long> eventIds) {
        if (eventIds == null) {
            return;
        }

        if (eventIds.size() == 0) {
            compilation.setEvents(List.of());
            return;
        }

        // Removing duplicates
        eventIds = new ArrayList<>(new HashSet<>(eventIds));

        List<Event> events = eventRepository.get(eventIds);

        if (events.size() != eventIds.size()) {
            throw new NotFoundException("Some events were not found");
        }

        compilation.setEvents(events);
    }

    @Override
    public CompilationDto create(NewCompilationDto dto) {
        Compilation compilation = mapper.toEntity(dto);

        mergeEvents(compilation, dto.getEvents());

        return mapper.toDto(repository.saveAndFlush(compilation), true);
    }

    @Override
    public CompilationDto get(Long id) {
        Compilation compilation = repository.get(id).orElse(null);

        if (compilation == null) {
            throw new NotFoundException("Compilation with id = " + id + " was not found");
        }

        return mapper.toDto(compilation, true);
    }

    @Override
    public List<CompilationDto> find(Boolean pinned, Integer from, Integer size) {
        Pageable pageRequest = OffsetPageRequest.ofOffset(from, size, null);

        List<Long> ids = repository.find(pinned, pageRequest);

        if (ids.size() == 0) {
            return List.of();
        }

        return mapper.toDto(repository.get(ids), true);
    }

    @Override
    public CompilationDto update(Long id, UpdateCompilationRequest dto) {
        Compilation compilation = (dto.getEvents() != null ? repository.findById(id) : repository.get(id))
                .orElse(null);

        if (compilation == null) {
            throw new NotFoundException("Compilation with id = " + id + " was not found");
        }

        mergeEvents(compilation, dto.getEvents());

        mapper.update(dto, compilation);

        return mapper.toDto(repository.saveAndFlush(compilation), true);
    }

    @Override
    public void delete(Long id) {
        try {
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException ignored) {
            throw new NotFoundException("Compilation with id = " + id + " was not found");
        }
    }
}