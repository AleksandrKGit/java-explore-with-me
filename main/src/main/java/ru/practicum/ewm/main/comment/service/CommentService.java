package ru.practicum.ewm.main.comment.service;

import ru.practicum.ewm.main.comment.dto.CommentDto;
import ru.practicum.ewm.main.comment.dto.CommentFullDto;
import ru.practicum.ewm.main.comment.dto.CommentShortDto;
import java.time.LocalDateTime;
import java.util.List;

public interface CommentService {
    CommentShortDto create(Long userId, Long eventId, CommentDto dto);

    List<CommentShortDto> findByCommentator(Long userId, String state, LocalDateTime start, LocalDateTime end,
                                            Integer from, Integer size);

    List<CommentFullDto> findByInitiator(Long userId, Long eventId, String state, LocalDateTime start,
                                         LocalDateTime end, Integer from, Integer size);

    CommentShortDto update(Long userId, Long id, CommentDto dto);

    List<CommentFullDto> rejectByInitiator(Long userId, List<Long> ids);

    List<CommentFullDto> publishByInitiator(Long userId, List<Long> ids);

    void delete(Long id);

    void deleteByCommentator(Long userId, Long id);
}