package ru.practicum.ewm.stat.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EndpointHit {
    String app;

    String uri;

    String ip;

    String timestamp;
}