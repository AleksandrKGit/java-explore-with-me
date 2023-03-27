package ru.practicum.ewm.main.compilation.Repository;

import ru.practicum.ewm.main.compilation.Compilation;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.List;

public class CompilationCustomRepositoryImpl implements CompilationCustomRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Long> findByPinned(Boolean pinned, Integer from, Integer size) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Compilation> compilations = query.from(Compilation.class);

        if (pinned != null) {
            query.where(cb.equal(compilations.get("pinned"), pinned));
        }

        return entityManager.createQuery(query
                        .select(compilations.get("id"))
                        .orderBy(cb.asc(compilations.get("id"))))
                .setFirstResult(from)
                .setMaxResults(size)
                .getResultList();
    }
}