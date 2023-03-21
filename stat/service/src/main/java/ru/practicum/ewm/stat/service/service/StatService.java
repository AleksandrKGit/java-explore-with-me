package ru.practicum.ewm.stat.service.service;

import ru.practicum.ewm.stat.dto.EndpointHitDto;
import ru.practicum.ewm.stat.dto.ViewStatsDto;
import java.time.LocalDateTime;
import java.util.List;

public interface StatService {
    void create(EndpointHitDto dto);

    List<ViewStatsDto> find(LocalDateTime start, LocalDateTime end, String[] uris, Boolean unique);
}