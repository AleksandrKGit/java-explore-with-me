package ru.practicum.ewm.main.stat;

import org.springframework.data.util.Pair;
import java.util.List;
import java.util.Map;

public interface StatService {
    List<Pair<Long, Long>> getViewsSortedByCount(List<Long> ids);

    Map<Long, Long> getViews(List<Long> ids);

    void addFindEventsEndPoint(String ip, String uri);

    void addEventEndPoint(String ip, Long id);
}
