package ru.practicum.ewm.main.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.main.request.model.Request;
import ru.practicum.ewm.main.request.model.RequestStatus;
import java.util.List;
import java.util.Set;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByIdInAndEvent_Id(Set<Long> ids, Long eventId);

    List<Request> findByRequestor_Id(Long userId);

    @Query("SELECT r FROM Request r WHERE r.event.id = ?2 AND r.event.initiator.id = ?1")
    List<Request> findByEventInitiator(Long userId, Long eventId);

    @Modifying
    @Query("UPDATE Request r SET r.status = ?2 WHERE r.id IN ?1")
    void updateStatus(Set<Long> ids, RequestStatus status);
}