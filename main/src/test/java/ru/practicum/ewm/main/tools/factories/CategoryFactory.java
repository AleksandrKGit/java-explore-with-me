package ru.practicum.ewm.main.tools.factories;

import ru.practicum.ewm.main.category.Category;
import ru.practicum.ewm.main.category.dto.NewCategoryDto;
import ru.practicum.ewm.main.category.dto.CategoryDto;
import java.util.Objects;

public class CategoryFactory {
    public static Category createCategory(Long id, String name) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        return category;
    }

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

    public static Category copyOf(Category category) {
        if (category == null) {
            return null;
        }

        Category copy = new Category();
        copy.setId(category.getId());
        copy.setName(category.getName());

        return copy;
    }

    public static boolean equals(Category category1, Category category2) {
        if (category1 == null && category2 == null) {
            return true;
        }

        return category1 != null && category2 != null
                && Objects.equals(category1.getId(), category2.getId())
                && Objects.equals(category1.getName(), category2.getName());
    }

    public static boolean equals(NewCategoryDto dto1, NewCategoryDto dto2) {
        if (dto1 == null && dto2 == null) {
            return true;
        }

        return dto1 != null && dto2 != null
                && Objects.equals(dto1.getName(), dto2.getName());
    }

    public static boolean equals(CategoryDto dto1, CategoryDto dto2) {
        if (dto1 == null && dto2 == null) {
            return true;
        }

        return dto1 != null && dto2 != null
                && Objects.equals(dto1.getName(), dto2.getName());
    }
}