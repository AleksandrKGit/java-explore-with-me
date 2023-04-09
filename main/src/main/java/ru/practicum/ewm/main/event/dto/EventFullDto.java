package ru.practicum.ewm.main.event.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.main.category.dto.CategoryDto;
import ru.practicum.ewm.main.event.model.Location;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventFullDto {
    Long id;

    UserShortDto initiator;

    CategoryDto category;

    String title;

    String annotation;

    String description;

    Location location;

    Boolean paid;

    Boolean requestModeration;

    Integer participantLimit;

    String createdOn;

    String publishedOn;

    String eventDate;

    String state;

    Long confirmedRequests;

    Long views;

    String commentsState;

    Long publishedComments;
}