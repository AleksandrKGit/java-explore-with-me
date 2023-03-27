package ru.practicum.ewm.main.compilation.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewCompilationDto {
    static final int MAX_TITLE_SIZE = 255;

    @Size(max = MAX_TITLE_SIZE, message = "size must be less or equal to: " + MAX_TITLE_SIZE)
    @NotBlank(message = "must not be blank")
    String title;

    @NotNull(message = "must not be null")
    Boolean pinned;

    List<Long> events;
}
