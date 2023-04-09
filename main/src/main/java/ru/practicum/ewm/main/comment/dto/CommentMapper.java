package ru.practicum.ewm.main.comment.dto;

import org.mapstruct.*;
import ru.practicum.ewm.main.comment.model.Comment;
import ru.practicum.ewm.main.comment.model.CommentState;
import ru.practicum.ewm.main.event.model.Event;
import ru.practicum.ewm.main.user.User;
import java.time.LocalDateTime;
import java.util.List;
import static ru.practicum.ewm.common.support.DateFactory.DATE_FORMAT;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {
    @Mapping(target = "eventDate", dateFormat = DATE_FORMAT)
    CommentEventDto toEventDto(Event event);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "commentator", source = "commentator")
    @Mapping(target = "event", source = "event")
    @Mapping(target = "createdOn", source = "createdOn")
    @Mapping(target = "state", source = "state")
    Comment toEntity(CommentDto dto, User commentator, Event event, LocalDateTime createdOn, CommentState state);

    @Mapping(target = "commentator", source = "comment.commentator.name")
    @Mapping(target = "createdOn", dateFormat = DATE_FORMAT)
    CommentPublicDto toPublicDto(Comment comment);

    List<CommentPublicDto> toPublicDto(List<Comment> comment);

    @Mapping(target = "createdOn", dateFormat = DATE_FORMAT)
    CommentFullDto toFullDto(Comment comment);

    List<CommentFullDto> toFullDto(List<Comment> comment);

    @Mapping(target = "createdOn", dateFormat = DATE_FORMAT)
    CommentShortDto toShortDto(Comment comment);

    List<CommentShortDto> toShortDto(List<Comment> comment);
}