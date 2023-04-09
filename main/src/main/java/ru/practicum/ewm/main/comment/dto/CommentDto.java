package ru.practicum.ewm.main.comment.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentDto {
    static final int MAX_TEXT_SIZE = 2000;

    @Size(max = MAX_TEXT_SIZE, message = "size must be less or equal to: " + MAX_TEXT_SIZE)
    @NotBlank(message = "must not be blank")
    String text;
}