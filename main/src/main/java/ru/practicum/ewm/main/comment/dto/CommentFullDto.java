package ru.practicum.ewm.main.comment.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.main.event.dto.UserShortDto;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentFullDto {
    Long id;

    UserShortDto commentator;

    CommentEventDto event;

    String createdOn;

    String text;

    String state;
}
