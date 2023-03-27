package ru.practicum.ewm.main.compilation.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.main.compilation.Compilation;
import java.util.List;
import java.util.Optional;

public interface CompilationRepository extends JpaRepository<Compilation, Long>, CompilationCustomRepository {
    @Query("SELECT c FROM Compilation c LEFT JOIN FETCH c.events e LEFT JOIN FETCH e.initiator " +
            "LEFT JOIN FETCH e.category WHERE c.id = ?1")
    Optional<Compilation> get(Long id);

    @Query(value = "SELECT c FROM Compilation c LEFT JOIN FETCH c.events e LEFT JOIN FETCH e.initiator " +
            "LEFT JOIN FETCH e.category WHERE c.id IN ?1")
    List<Compilation> get(List<Long> ids);
}