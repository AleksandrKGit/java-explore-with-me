package ru.practicum.ewm.main.event.service;

import ru.practicum.ewm.main.event.controller.EventSort;
import ru.practicum.ewm.main.event.dto.*;
import ru.practicum.ewm.main.request.dto.ParticipationRequestDto;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto create(Long userId, NewEventDto dto);

    EventFullDto get(String ip, Long id);

    EventFullDto getByInitiator(Long userId, Long id);

    List<EventShortDto> find(String ip, String uri, String text, List<Long> categoryIds, Boolean paid,
                             LocalDateTime start, LocalDateTime end, Boolean onlyAvailable, EventSort sort,
                             Integer from, Integer size);

    List<EventShortDto> findByInitiator(Long userId, Integer from, Integer size);

    List<EventFullDto> findByAdmin(List<Long> userIds, List<String> states, List<Long> categoryIds, LocalDateTime start,
                                   LocalDateTime end, Integer from, Integer size);

    EventFullDto updateByInitiator(Long userId, Long id, UpdateEventUserRequest dto);

    EventFullDto updateByAdmin(Long id, UpdateEventAdminRequest dto);

    List<ParticipationRequestDto> findRequestsByInitiator(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequestsByInitiator(Long userId, Long eventId, EventRequestStatusUpdateRequest dto);
}