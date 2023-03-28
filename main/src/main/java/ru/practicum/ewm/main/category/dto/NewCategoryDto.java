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
public class NewCategoryDto {
    static final int MAX_NAME_SIZE = 255;

    @Size(max = MAX_NAME_SIZE, message = "size must be less or equal to: " + MAX_NAME_SIZE)
    @NotBlank(message = "must not be blank")
    String name;
}
