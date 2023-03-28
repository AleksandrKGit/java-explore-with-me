package ru.practicum.ewm.main.event.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.main.event.model.Location;
import ru.practicum.ewm.main.validation.constraints.DateOfPattern;
import ru.practicum.ewm.main.validation.constraints.EventUserSate;
import ru.practicum.ewm.main.validation.constraints.NullOrNotBlank;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import static ru.practicum.ewm.common.support.DateFactory.*;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventUserRequest {
    static final int MAX_ANNOTATION_SIZE = 2000;

    static final int MIN_TITLE_SIZE = 3;

    static final int MAX_TITLE_SIZE = 120;

    static final int MIN_ANNOTATION_SIZE = 20;

    static final int MAX_DESCRIPTION_SIZE = 7000;

    static final int MIN_DESCRIPTION_SIZE = 20;

    Long category;

    @Size(min = MIN_TITLE_SIZE, max = MAX_TITLE_SIZE, message = "size must be between " + MIN_TITLE_SIZE
            + " AND " + MAX_TITLE_SIZE + " including bounds")
    @NullOrNotBlank
    String title;

    @Size(min = MIN_ANNOTATION_SIZE, max = MAX_ANNOTATION_SIZE, message = "size must be between " + MIN_ANNOTATION_SIZE
            + " AND " + MAX_ANNOTATION_SIZE + " including bounds")
    @NullOrNotBlank
    String annotation;

    @Size(min = MIN_DESCRIPTION_SIZE, max = MAX_DESCRIPTION_SIZE, message = "size must be between "
            + MIN_DESCRIPTION_SIZE + " AND " + MAX_DESCRIPTION_SIZE + " including bounds")
    @NullOrNotBlank
    String description;

    Location location;

    Boolean paid;

    Boolean requestModeration;

    @Min(value = 0, message = "must not be less than 0")
    Integer participantLimit;

    @DateOfPattern(pattern = DATE_FORMAT, message = "must be of pattern: " + DATE_FORMAT)
    String eventDate;

    @EventUserSate
    String stateAction;
}