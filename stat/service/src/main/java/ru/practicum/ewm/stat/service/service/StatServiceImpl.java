package ru.practicum.ewm.stat.service.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stat.dto.EndpointHitDto;
import ru.practicum.ewm.stat.dto.ViewStatsDto;
import ru.practicum.ewm.stat.service.StatMapper;
import ru.practicum.ewm.stat.service.StatRepository;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatServiceImpl implements StatService {
    StatRepository repository;

    StatMapper mapper;

    @Override
    public void create(EndpointHitDto dto) {
        repository.saveAndFlush(mapper.toEntity(dto));
    }

    @Override
    public List<ViewStatsDto> find(LocalDateTime start, LocalDateTime end, String[] uris, Boolean unique) {
        return unique ? repository.findUnique(start, end, uris) : repository.find(start, end, uris);
    }
}