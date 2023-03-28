package ru.practicum.ewm.stat.service.service;

import ru.practicum.ewm.stat.dto.EndpointHit;
import ru.practicum.ewm.stat.dto.ViewStats;
import java.time.LocalDateTime;
import java.util.List;

public interface StatService {
    void create(EndpointHit dto);

    List<ViewStats> find(LocalDateTime start, LocalDateTime end, String[] uris, Boolean unique);
}