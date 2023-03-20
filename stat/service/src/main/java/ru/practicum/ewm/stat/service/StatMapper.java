package ru.practicum.ewm.stat.service;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.stat.dto.EndpointHitDto;
import ru.practicum.ewm.stat.service.model.EndpointHit;

@Mapper(componentModel = "spring")
public interface StatMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "timestamp", dateFormat = "yyyy-MM-dd HH:mm:ss")
    EndpointHit toEntity(EndpointHitDto dto);
}