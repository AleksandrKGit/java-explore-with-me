package ru.practicum.ewm.main.tools.factories;

import ru.practicum.ewm.main.compilation.Compilation;
import ru.practicum.ewm.main.compilation.dto.CompilationDto;
import ru.practicum.ewm.main.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.main.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.main.event.dto.EventShortDto;
import ru.practicum.ewm.main.event.model.Event;
import java.util.List;
import java.util.Objects;

public class CompilationFactory {
    public static Compilation createCompilation(Long id, String title, Boolean pinned, List<Event> events) {
        Compilation compilation = new Compilation();

        compilation.setId(id);
        compilation.setTitle(title);
        compilation.setPinned(pinned);
        compilation.setEvents(events);

        return compilation;
    }

    public static CompilationDto createCompilationDto(Long id, String title, Boolean pinned,
                                                      List<EventShortDto> events) {
        CompilationDto dto = new CompilationDto();

        dto.setId(id);
        dto.setTitle(title);
        dto.setPinned(pinned);
        dto.setEvents(events);

        return dto;
    }

    public static NewCompilationDto createNewCompilationDto(String title, Boolean pinned, List<Long> events) {
        NewCompilationDto dto = new NewCompilationDto();

        dto.setTitle(title);
        dto.setPinned(pinned);
        dto.setEvents(events);

        return dto;
    }

    public static UpdateCompilationRequest createUpdateCompilationRequest(String title, Boolean pinned,
                                                                          List<Long> events) {
        UpdateCompilationRequest dto = new UpdateCompilationRequest();

        dto.setTitle(title);
        dto.setPinned(pinned);
        dto.setEvents(events);

        return dto;
    }

    public static Compilation copyOf(Compilation compilation) {
        if (compilation == null) {
            return null;
        }

        Compilation copy = new Compilation();

        copy.setId(compilation.getId());
        copy.setTitle(compilation.getTitle());
        copy.setPinned(compilation.getPinned());
        copy.setEvents(compilation.getEvents());

        return copy;
    }

    public static boolean equals(Compilation compilation1, Compilation compilation2) {
        if (compilation1 == null && compilation2 == null) {
            return true;
        }

        return compilation1 != null && compilation2 != null
                && Objects.equals(compilation1.getId(), compilation2.getId())
                && Objects.equals(compilation1.getTitle(), compilation2.getTitle())
                && Objects.equals(compilation1.getPinned(), compilation2.getPinned())
                && Objects.equals(compilation1.getEvents(), compilation2.getEvents());
    }

    public static boolean equals(CompilationDto dto1, CompilationDto dto2) {
        if (dto1 == null && dto2 == null) {
            return true;
        }

        return dto1 != null && dto2 != null
                && Objects.equals(dto1.getId(), dto2.getId())
                && Objects.equals(dto1.getTitle(), dto2.getTitle())
                && Objects.equals(dto1.getPinned(), dto2.getPinned())
                && EventFactory.equals(dto1.getEvents(), dto2.getEvents());
    }

    public static boolean equals(NewCompilationDto dto1, NewCompilationDto dto2) {
        if (dto1 == null && dto2 == null) {
            return true;
        }

        return dto1 != null && dto2 != null
                && Objects.equals(dto1.getTitle(), dto2.getTitle())
                && Objects.equals(dto1.getPinned(), dto2.getPinned())
                && Objects.equals(dto1.getEvents(), dto2.getEvents());
    }

    public static boolean equals(UpdateCompilationRequest dto1, UpdateCompilationRequest dto2) {
        if (dto1 == null && dto2 == null) {
            return true;
        }

        return dto1 != null && dto2 != null
                && Objects.equals(dto1.getTitle(), dto2.getTitle())
                && Objects.equals(dto1.getPinned(), dto2.getPinned())
                && Objects.equals(dto1.getEvents(), dto2.getEvents());
    }
}