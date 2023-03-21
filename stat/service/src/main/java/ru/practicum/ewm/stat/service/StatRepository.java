package ru.practicum.ewm.stat.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.stat.dto.ViewStatsDto;
import ru.practicum.ewm.stat.service.model.EndpointHit;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatRepository extends JpaRepository<EndpointHit, Long> {
    @Query("SELECT new ru.practicum.ewm.stat.dto.ViewStatsDto(e.app, e.uri, COUNT(e.ip)) " +
            "FROM EndpointHit AS e WHERE e.timestamp >= ?1 AND e.timestamp <= ?2 GROUP BY e.app, e.uri " +
            "HAVING e.uri IN ?3 ORDER BY COUNT(e.ip) DESC")
    List<ViewStatsDto> find(LocalDateTime start, LocalDateTime end, String[] uris);

    @Query("SELECT new ru.practicum.ewm.stat.dto.ViewStatsDto(e.app, e.uri, COUNT(DISTINCT e.ip)) " +
            "FROM EndpointHit AS e WHERE e.timestamp >= ?1 AND e.timestamp <= ?2 GROUP BY e.app, e.uri " +
            "HAVING e.uri IN ?3 ORDER BY COUNT(DISTINCT e.ip) DESC")
    List<ViewStatsDto> findUnique(LocalDateTime start, LocalDateTime end, String[] uris);
}