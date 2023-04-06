package ru.practicum.ewm.main.comment.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.comment.repository.CommentRepository;
import ru.practicum.ewm.main.comment.dto.CommentDto;
import ru.practicum.ewm.main.comment.dto.CommentFullDto;
import ru.practicum.ewm.main.comment.dto.CommentMapper;
import ru.practicum.ewm.main.comment.dto.CommentShortDto;
import ru.practicum.ewm.main.comment.model.Comment;
import ru.practicum.ewm.main.comment.model.CommentState;
import ru.practicum.ewm.main.event.model.Event;
import ru.practicum.ewm.main.event.model.EventCommentsState;
import ru.practicum.ewm.main.event.model.EventState;
import ru.practicum.ewm.main.event.repository.EventRepository;
import ru.practicum.ewm.main.exception.ConflictException;
import ru.practicum.ewm.main.exception.NotFoundException;
import ru.practicum.ewm.main.support.OffsetPageRequest;
import ru.practicum.ewm.main.user.User;
import ru.practicum.ewm.main.user.UserRepository;
import java.time.LocalDateTime;
import java.util.*;
import static ru.practicum.ewm.main.entity.EntityWithId.sortByIds;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentServiceImpl implements CommentService {
    CommentMapper mapper;

    CommentRepository repository;

    UserRepository userRepository;

    EventRepository eventRepository;

    @Transactional
    @Override
    public CommentShortDto create(Long userId, Long eventId, CommentDto dto) {
        Event event = eventRepository.findById(eventId).orElse(null);

        if (event == null) {
            throw new NotFoundException("Event with id = " + eventId + " was not found");
        }

        User commentator = userRepository.findById(userId).orElse(null);

        if (commentator == null) {
            throw new NotFoundException("User with id = " + userId + " was not found");
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Event with id = " + eventId + " was not published");
        }

        if (event.getCommentsState().equals(EventCommentsState.NOT_ALLOWED)
                && !event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Comments to event with id = " + eventId + " are not allowed");
        }

        boolean doPublish = event.getInitiator().getId().equals(userId) ||
                event.getCommentsState().equals(EventCommentsState.PUBLIC);

        Comment comment = mapper.toEntity(dto, commentator, event, LocalDateTime.now(),
                doPublish ? CommentState.PUBLISHED : CommentState.PENDING);

        comment = repository.saveAndFlush(comment);

        if (doPublish) {
            eventRepository.increasePublishedComments(eventId, 1L);
        }

        return mapper.toShortDto(comment);
    }

    @Override
    public List<CommentShortDto> findByCommentator(Long userId, String state, LocalDateTime start,
                                                   LocalDateTime end, Integer from, Integer size) {
        Pageable pageRequest = OffsetPageRequest.ofOffset(from, size, Sort.by("createdOn").descending());

        List<Long> ids = repository.findByQuery(null, userId, null,
                state == null ? null : CommentState.valueOf(state), start, end, pageRequest);

        if (ids.size() == 0) {
            return List.of();
        }

        return mapper.toShortDto(sortByIds(repository.getByCommentator(ids), ids));
    }

    @Override
    public List<CommentFullDto> findByInitiator(Long userId, Long eventId, String state, LocalDateTime start,
                                                LocalDateTime end, Integer from, Integer size) {
        Pageable pageRequest = OffsetPageRequest.ofOffset(from, size, Sort.by("createdOn").descending());

        List<Long> ids = repository.findByQuery(userId, null, eventId,
                state == null ? null : CommentState.valueOf(state), start, end, pageRequest);

        if (ids.size() == 0) {
            return List.of();
        }

        return mapper.toFullDto(sortByIds(repository.getByInitiator(ids), ids));
    }

    @Transactional
    @Override
    public CommentShortDto update(Long userId, Long id, CommentDto dto) {
        Comment comment = repository.getByCommentator(id, userId).orElse(null);

        if (comment == null) {
            throw new NotFoundException("Comment with id = " + id + " was not found");
        }

        comment.setText(dto.getText());

        if (!comment.getEvent().getInitiator().getId().equals(userId)) {
            if (comment.getState().equals(CommentState.PUBLISHED)
                    && comment.getEvent().getCommentsState().equals(EventCommentsState.MODERATED)) {
                comment.setState(CommentState.PENDING);
                eventRepository.decreasePublishedComments(comment.getEvent().getId(), 1L);
            } else if (comment.getState().equals(CommentState.REJECTED)
                    && comment.getEvent().getCommentsState().equals(EventCommentsState.MODERATED)) {
                comment.setState(CommentState.PENDING);
            } else if (comment.getState().equals(CommentState.REJECTED)
                    && comment.getEvent().getCommentsState().equals(EventCommentsState.PUBLIC)) {
                comment.setState(CommentState.PUBLISHED);
                eventRepository.increasePublishedComments(comment.getEvent().getId(), 1L);
            }
        }

        return mapper.toShortDto(repository.saveAndFlush(comment));
    }

    @Transactional
    @Override
    public List<CommentFullDto> rejectByInitiator(Long userId, List<Long> ids) {
        ids = new ArrayList<>((new HashSet<>(ids)));

        List<Comment> comments = repository.getByInitiator(ids, userId, List.of(CommentState.PENDING,
                CommentState.PUBLISHED));

        if (ids.size() != comments.size()) {
            throw new NotFoundException("Not all pending or published comments found");
        }

        Map<Long, Long> decreaseCount = new TreeMap<>(Long::compareTo);
        comments.forEach(comment -> {
            if (comment.getState().equals(CommentState.PUBLISHED)) {
                decreaseCount.put(comment.getEvent().getId(),
                        decreaseCount.getOrDefault(comment.getEvent().getId(), 0L) + 1L);
            }

            comment.setState(CommentState.REJECTED);
        });

        repository.saveAllAndFlush(comments);

        decreaseCount.forEach(eventRepository::decreasePublishedComments);

        return mapper.toFullDto(comments);
    }

    @Transactional
    @Override
    public List<CommentFullDto> publishByInitiator(Long userId, List<Long> ids) {
        ids = new ArrayList<>((new HashSet<>(ids)));

        List<Comment> comments = repository.getByInitiator(ids, userId, List.of(CommentState.PENDING));

        if (ids.size() != comments.size()) {
            throw new NotFoundException("Not all pending comments found");
        }

        Map<Long, Long> increaseCount = new TreeMap<>(Long::compareTo);
        comments.forEach(comment -> {
            increaseCount.put(comment.getEvent().getId(),
                    increaseCount.getOrDefault(comment.getEvent().getId(), 0L) + 1L);

            comment.setState(CommentState.PUBLISHED);
        });

        repository.saveAllAndFlush(comments);

        increaseCount.forEach(eventRepository::increasePublishedComments);

        return mapper.toFullDto(comments);
    }

    private void delete(Long id, Comment comment) {
        if (comment == null) {
            throw new NotFoundException("Comment with id = " + id + " was not found");
        }

        if (comment.getState().equals(CommentState.PUBLISHED)) {
            eventRepository.decreasePublishedComments(comment.getEvent().getId(), 1L);
        }

        repository.delete(comment);
    }

    @Transactional
    @Override
    public void deleteByCommentator(Long userId, Long id) {
        delete(id, repository.getByCommentator(id, userId).orElse(null));
    }

    @Transactional
    @Override
    public void delete(Long id) {
        delete(id, repository.findById(id).orElse(null));
    }
}