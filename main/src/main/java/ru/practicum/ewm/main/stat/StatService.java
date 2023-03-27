package ru.practicum.ewm.main.stat;

import org.springframework.data.util.Pair;
import ru.practicum.ewm.main.event.controller.EventSort;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface StatService {
    List<Pair<Long, Long>> getViewsSortedByCount(List<Long> ids);

    Map<Long, Long> getViews(List<Long> ids);

    void addFindEventsEndPoint(String ip, String text, List<Long> categoryIds, Boolean paid, LocalDateTime start,
                               LocalDateTime end, Boolean onlyAvailable, EventSort sort, Integer from,
                               Integer size);

    void addEventEndPoint(String ip, Long id);
}
