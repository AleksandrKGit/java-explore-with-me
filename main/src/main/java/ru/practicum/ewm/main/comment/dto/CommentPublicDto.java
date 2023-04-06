package ru.practicum.ewm.main.comment.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentPublicDto {
    Long id;

    String createdOn;

    String commentator;

    String text;
}