package ru.practicum.ewm.main.comment.repository;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.main.comment.model.CommentState;
import java.time.LocalDateTime;
import java.util.List;

public interface CommentCustomRepository {
    List<Long> findByQuery(Long initiatorId, Long commentatorId, Long eventId, CommentState state, LocalDateTime start,
                           LocalDateTime end, Pageable pageable);
}
