package ru.practicum.ewm.main.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.main.event.model.Event;
import ru.practicum.ewm.main.event.model.EventState;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, EventCustomRepository {
    @Query("SELECT e FROM Event e INNER JOIN e.initiator INNER JOIN e.category WHERE e.id = ?1")
    Optional<Event> get(Long id);

    @Query("SELECT e FROM Event e JOIN FETCH e.initiator JOIN FETCH e.category WHERE e.id IN ?1")
    List<Event> get(List<Long> ids);

    @Query("SELECT e FROM Event e JOIN FETCH e.initiator JOIN FETCH e.category WHERE e.id = ?1 AND e.state = ?2")
    Optional<Event> getByState(Long id, EventState state);

    @Modifying
    @Query("UPDATE Event e SET e.confirmedRequests = e.confirmedRequests + ?2 WHERE e.id = ?1")
    void increaseConfirmedEvents(Long id, Long count);

    @Modifying
    @Query("UPDATE Event e SET e.confirmedRequests = e.confirmedRequests - 1 WHERE e.id = ?1")
    void decrementConfirmedEvents(Long id);

    @Query("SELECT e.id FROM Event e WHERE e.initiator.id = ?1")
    List<Long> findByInitiator(Long userId, Pageable pageable);
}