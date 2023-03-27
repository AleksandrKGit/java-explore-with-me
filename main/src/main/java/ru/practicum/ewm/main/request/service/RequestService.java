package ru.practicum.ewm.main.request.service;

import ru.practicum.ewm.main.request.dto.ParticipationRequestDto;
import java.util.List;

public interface RequestService {
    ParticipationRequestDto create(Long userId, Long eventId);

    List<ParticipationRequestDto> find(Long userId);

    ParticipationRequestDto cancel(Long userId, Long id);
}