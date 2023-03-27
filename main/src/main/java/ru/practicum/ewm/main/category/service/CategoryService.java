package ru.practicum.ewm.main.category.service;

import ru.practicum.ewm.main.category.dto.CategoryDto;
import ru.practicum.ewm.main.category.dto.NewCategoryDto;
import java.util.List;

public interface CategoryService {
    CategoryDto create(NewCategoryDto dto);

    CategoryDto get(Long id);

    List<CategoryDto> find(Integer from, Integer size);

    CategoryDto update(Long id, CategoryDto dto);

    void delete(Long id);
}
