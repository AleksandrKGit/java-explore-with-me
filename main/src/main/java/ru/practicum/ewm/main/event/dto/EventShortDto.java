package ru.practicum.ewm.main.event.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.main.category.dto.CategoryDto;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventShortDto {
    Long id;

    UserShortDto initiator;

    CategoryDto category;

    String title;

    String annotation;

    Boolean paid;

    String eventDate;

    Long confirmedRequests;

    Long views;

    String commentsState;

    Long publishedComments;
}