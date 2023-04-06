package ru.practicum.ewm.main.comment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.main.comment.model.Comment;
import ru.practicum.ewm.main.comment.model.CommentState;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>, CommentCustomRepository {
    @Query("SELECT c FROM Comment c JOIN FETCH c.commentator WHERE c.id IN ?1 ORDER BY c.createdOn DESC")
    List<Comment> getWithCommentator(List<Long> ids);

    @Query("SELECT c FROM Comment c JOIN FETCH c.event WHERE c.id = ?1 AND c.commentator.id = ?2")
    Optional<Comment> getByCommentator(Long id, Long commentator);

    @Query("SELECT c FROM Comment c JOIN FETCH c.event WHERE c.id IN ?1 ORDER BY c.createdOn DESC")
    List<Comment> getByCommentator(List<Long> ids);

    @Query("SELECT c FROM Comment c JOIN FETCH c.commentator JOIN FETCH c.event WHERE c.id IN ?1 "
            + "ORDER BY c.createdOn DESC")
    List<Comment> getByInitiator(List<Long> ids);

    @Query("SELECT c FROM Comment c JOIN FETCH c.commentator JOIN FETCH c.event WHERE c.id IN ?1 "
            + "AND c.event.initiator.id = ?2 AND c.commentator.id <> ?2 AND c.state IN ?3 ORDER BY c.createdOn DESC")
    List<Comment> getByInitiator(List<Long> ids, Long userId, List<CommentState> states);

    @Query("SELECT c.id FROM Comment c WHERE c.event.id = ?1 AND c.state = ?2")
    List<Long> findByEventAndState(Long eventId, CommentState state, Pageable pageable);
}