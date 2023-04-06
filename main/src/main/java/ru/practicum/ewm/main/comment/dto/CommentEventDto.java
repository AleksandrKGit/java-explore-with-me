package ru.practicum.ewm.main.comment.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentEventDto {
    Long id;

    String title;

    String eventDate;

    String commentsState;
}
