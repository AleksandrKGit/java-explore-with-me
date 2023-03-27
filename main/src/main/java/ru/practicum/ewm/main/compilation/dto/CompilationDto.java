package ru.practicum.ewm.main.compilation.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.main.event.dto.EventShortDto;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationDto {
    Long id;

    String title;

    Boolean pinned;

    List<EventShortDto> events = List.of();
}
