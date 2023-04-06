package ru.practicum.ewm.main.entity;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public interface EntityWithId {
    Long getId();

    static <T extends EntityWithId> List<T> sortByIds(List<T> entities, List<Long> ids) {
        Map<Long, T> map = new TreeMap<>(Long::compareTo);
        entities.forEach(e -> map.put(e.getId(), e));

        return ids.stream().map(map::get).collect(Collectors.toList());
    }
}