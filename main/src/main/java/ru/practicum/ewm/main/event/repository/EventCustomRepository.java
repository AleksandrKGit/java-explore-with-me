package ru.practicum.ewm.main.event.repository;

import ru.practicum.ewm.main.event.controller.EventSort;
import ru.practicum.ewm.main.event.model.EventState;
import java.time.LocalDateTime;
import java.util.List;

public interface EventCustomRepository {
    List<Long> findByQuery(String text, List<Long> categoryIds, Boolean paid, LocalDateTime start,
                           LocalDateTime end, Boolean onlyAvailable, Integer from, Integer size, EventSort sort);

    List<Long> findByAdmin(List<Long> userIds, List<EventState> states, List<Long> categoryIds, LocalDateTime start,
                           LocalDateTime end, Integer from, Integer size);
}
