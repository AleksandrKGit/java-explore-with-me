package ru.practicum.ewm.stat.service;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.stat.dto.EndpointHit;
import ru.practicum.ewm.stat.service.model.Hit;

@Mapper(componentModel = "spring")
public interface StatMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "timestamp", dateFormat = "yyyy-MM-dd HH:mm:ss")
    Hit toEntity(EndpointHit dto);
}