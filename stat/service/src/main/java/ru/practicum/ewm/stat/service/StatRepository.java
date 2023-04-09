package ru.practicum.ewm.stat.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.stat.dto.ViewStats;
import ru.practicum.ewm.stat.service.model.Hit;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatRepository extends JpaRepository<Hit, Long> {
    @Query("SELECT new ru.practicum.ewm.stat.dto.ViewStats(h.app, h.uri, COUNT(h.ip)) " +
            "FROM Hit h WHERE h.timestamp >= ?1 AND h.timestamp <= ?2 GROUP BY h.app, h.uri " +
            "HAVING h.uri IN ?3 ORDER BY COUNT(h.ip) DESC")
    List<ViewStats> findWithUris(LocalDateTime start, LocalDateTime end, String[] uris);

    @Query("SELECT new ru.practicum.ewm.stat.dto.ViewStats(h.app, h.uri, COUNT(DISTINCT h.ip)) " +
            "FROM Hit h WHERE h.timestamp >= ?1 AND h.timestamp <= ?2 GROUP BY h.app, h.uri " +
            "HAVING h.uri IN ?3 ORDER BY COUNT(DISTINCT h.ip) DESC")
    List<ViewStats> findUniqueWithUris(LocalDateTime start, LocalDateTime end, String[] uris);

    @Query("SELECT new ru.practicum.ewm.stat.dto.ViewStats(h.app, h.uri, COUNT(h.ip)) " +
            "FROM Hit h WHERE h.timestamp >= ?1 AND h.timestamp <= ?2 GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(h.ip) DESC")
    List<ViewStats> find(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.ewm.stat.dto.ViewStats(h.app, h.uri, COUNT(DISTINCT h.ip)) " +
            "FROM Hit h WHERE h.timestamp >= ?1 AND h.timestamp <= ?2 GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(DISTINCT h.ip) DESC")
    List<ViewStats> findUnique(LocalDateTime start, LocalDateTime end);
}