package ru.practicum.ewm.main.compilation;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    @Query("SELECT c FROM Compilation c LEFT JOIN FETCH c.events e LEFT JOIN FETCH e.initiator " +
            "LEFT JOIN FETCH e.category WHERE c.id = ?1")
    Optional<Compilation> get(Long id);

    @Query(value = "SELECT c FROM Compilation c LEFT JOIN FETCH c.events e LEFT JOIN FETCH e.initiator " +
            "LEFT JOIN FETCH e.category WHERE c.id IN ?1")
    List<Compilation> get(List<Long> ids);

    @Query("SELECT c.id FROM Compilation c WHERE ?1 IS NULL OR c.pinned = ?1")
    List<Long> find(Boolean pinned, Pageable pageable);
}