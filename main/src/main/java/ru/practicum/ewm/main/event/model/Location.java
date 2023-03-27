package ru.practicum.ewm.main.event.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import javax.persistence.Embeddable;

@Embeddable
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Location {
    float lat;

    float lon;
}
