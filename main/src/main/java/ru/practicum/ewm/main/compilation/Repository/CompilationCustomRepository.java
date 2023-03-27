package ru.practicum.ewm.main.compilation.Repository;

import java.util.List;

public interface CompilationCustomRepository {
    List<Long> findByPinned(Boolean pinned, Integer from, Integer size);
}