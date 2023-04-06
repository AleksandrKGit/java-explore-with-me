package ru.practicum.ewm.stat.service.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stat.dto.EndpointHit;
import ru.practicum.ewm.stat.dto.ViewStats;
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
    public void create(EndpointHit dto) {
        repository.saveAndFlush(mapper.toEntity(dto));
    }

    @Override
    public List<ViewStats> find(LocalDateTime start, LocalDateTime end, String[] uris, Boolean unique) {
        if (uris == null) {
            return unique ? repository.findUnique(start, end) : repository.find(start, end);
        }

        return unique ? repository.findUniqueWithUris(start, end, uris) : repository.findWithUris(start, end, uris);
    }
}