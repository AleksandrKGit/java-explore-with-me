package ru.practicum.ewm.main.comment.repository;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.main.comment.model.Comment;
import ru.practicum.ewm.main.comment.model.CommentState;
import org.springframework.data.jpa.repository.query.QueryUtils;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommentCustomRepositoryImpl implements CommentCustomRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Long> findByQuery(Long initiatorId, Long commentatorId, Long eventId, CommentState state, LocalDateTime start,
                                       LocalDateTime end, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Comment> comments = query.from(Comment.class);
        query.orderBy(QueryUtils.toOrders(pageable.getSort(), comments, cb));

        List<Predicate> predicates = new ArrayList<>();

        if (initiatorId != null) {
            predicates.add(cb.equal(comments.get("event").get("initiator").get("id"), initiatorId));
        }

        if (commentatorId != null) {
            predicates.add(cb.equal(comments.get("commentator").get("id"), commentatorId));
        }

        if (eventId != null) {
            predicates.add(cb.equal(comments.get("event").get("id"), eventId));
        }

        if (state != null) {
            predicates.add(cb.equal(comments.get("state"), state));
        }

        if (start != null) {
            predicates.add(cb.greaterThanOrEqualTo(comments.get("createdOn"), start));
        }

        if (end != null) {
            predicates.add(cb.lessThanOrEqualTo(comments.get("createdOn"), end));
        }

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        return entityManager.createQuery(query.select(comments.get("id")))
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
    }
}