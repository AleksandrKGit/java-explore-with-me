package ru.practicum.ewm.main.event.repository;

import ru.practicum.ewm.main.event.controller.EventSort;
import ru.practicum.ewm.main.event.model.Event;
import ru.practicum.ewm.main.event.model.EventState;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventCustomRepositoryImpl implements EventCustomRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Long> findByQuery(String text, List<Long> categoryIds, Boolean paid, LocalDateTime start,
                                  LocalDateTime end, Boolean onlyAvailable, Integer from, Integer size,
                                  EventSort sort) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Event> events = query.from(Event.class);

        if (EventSort.EVENT_DATE.equals(sort)) {
            query.orderBy(cb.desc(events.get("eventDate")));
        }

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(events.get("state"), EventState.PUBLISHED));

        if (text != null) {
            predicates.add(cb.or(cb.like(cb.upper(events.get("description")), "%" + text.toUpperCase() + "%"),
                    cb.like(cb.upper(events.get("annotation")), "%" + text.toUpperCase() + "%")));
        }

        if (categoryIds != null && !categoryIds.isEmpty()) {
            predicates.add(events.get("category").get("id").in(categoryIds));
        }

        if (paid != null) {
            predicates.add(cb.equal(events.get("paid"), paid));
        }

        if (start == null && end == null) {
            predicates.add(cb.greaterThan(events.get("eventDate"), LocalDateTime.now()));
        } else {
            if (start != null) {
                predicates.add(cb.greaterThanOrEqualTo(events.get("eventDate"), start));
            }

            if (end != null) {
                predicates.add(cb.lessThanOrEqualTo(events.get("eventDate"), end));
            }
        }

        if (onlyAvailable != null && onlyAvailable) {
            predicates.add(cb.or(cb.equal(events.get("participantLimit"), 0),
                    cb.greaterThan(events.get("participantLimit"), events.get("confirmedRequests"))));
        }

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        return entityManager.createQuery(query.select(events.get("id")))
                .setFirstResult(from)
                .setMaxResults(size)
                .getResultList();
    }

    @Override
    public List<Long> findByAdmin(List<Long> userIds, List<EventState> states, List<Long> categoryIds,
                                  LocalDateTime start, LocalDateTime end, Integer from, Integer size) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Event> events = query.from(Event.class);

        List<Predicate> predicates = new ArrayList<>();

        if (userIds != null && !userIds.isEmpty()) {
            predicates.add(events.get("initiator").get("id").in(userIds));
        }

        if (states != null &&  !states.isEmpty()) {
            predicates.add(events.get("state").in(states));
        }

        if (categoryIds != null) {
            predicates.add(events.get("category").get("id").in(categoryIds));
        }

        if (start != null) {
            predicates.add(cb.greaterThanOrEqualTo(events.get("eventDate"), start));
        }

        if (end != null) {
            predicates.add(cb.lessThanOrEqualTo(events.get("eventDate"), end));
        }

        if (predicates.size() != 0) {
            query.where(predicates.size() == 1 ? predicates.get(0) : cb.and(predicates.toArray(new Predicate[0])));
        }

        return entityManager.createQuery(query.select(events.get("id")))
                .setFirstResult(from)
                .setMaxResults(size)
                .getResultList();
    }
}