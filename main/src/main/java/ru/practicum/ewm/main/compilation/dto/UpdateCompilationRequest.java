package ru.practicum.ewm.main.compilation.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.main.validation.constraints.NullOrNotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateCompilationRequest {
    static final int MAX_TITLE_SIZE = 255;

    @Size(max = MAX_TITLE_SIZE, message = "size must be less or equal to: " + MAX_TITLE_SIZE)
    @NullOrNotBlank
    String title;

    Boolean pinned;

    List<Long> events;
}
