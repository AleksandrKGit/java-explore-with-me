package ru.practicum.ewm.main.request.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.main.request.model.Request;
import java.util.List;

@Mapper(componentModel = "spring")
public interface RequestMapper {
    @Mapping(target = "requester", source = "request.requestor.id")
    @Mapping(target = "event", source = "request.event.id")
    ParticipationRequestDto toDto(Request request);

    List<ParticipationRequestDto> toDto(List<Request> requests);
}