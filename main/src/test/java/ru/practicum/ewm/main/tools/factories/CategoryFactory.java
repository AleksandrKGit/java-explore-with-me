package ru.practicum.ewm.main.tools.factories;

import ru.practicum.ewm.main.category.Category;
import ru.practicum.ewm.main.category.dto.NewCategoryDto;
import ru.practicum.ewm.main.category.dto.CategoryDto;

public class CategoryFactory {
    public static NewCategoryDto createNewCategoryDto(String name) {
        NewCategoryDto dto = new NewCategoryDto();
        dto.setName(name);
        return dto;
    }

    public static CategoryDto createCategoryDto(Long id, String name) {
        CategoryDto dto = new CategoryDto();
        dto.setId(id);
        dto.setName(name);
        return dto;
    }

    public static Category createCategory(Long id, String name) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        return category;
    }

    public static Category copyOf(Category category) {
        if (category == null) {
            return null;
        }

        Category copy = new Category();
        copy.setId(category.getId());
        copy.setName(category.getName());

        return copy;
    }
}