package ru.practicum.ewm.main.category.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryDto {
    static final int MAX_NAME_SIZE = 255;

    Long id;

    @Size(max = MAX_NAME_SIZE, message = "size must be less or equal to: " + MAX_NAME_SIZE)
    @NotBlank(message = "must not be blank")
    String name;
}
