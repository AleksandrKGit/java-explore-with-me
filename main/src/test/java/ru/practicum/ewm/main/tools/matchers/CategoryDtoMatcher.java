package ru.practicum.ewm.main.tools.matchers;

import org.mockito.ArgumentMatcher;
import ru.practicum.ewm.main.category.dto.CategoryDto;
import java.util.Objects;

public class CategoryDtoMatcher implements ArgumentMatcher<CategoryDto> {
    private final CategoryDto dto;

    private CategoryDtoMatcher(CategoryDto dto) {
        this.dto = dto;
    }

    public static CategoryDtoMatcher equalTo(CategoryDto dto) {
        return new CategoryDtoMatcher(dto);
    }

    @Override
    public boolean matches(CategoryDto dto) {
        return dto != null && this.dto != null
                && Objects.equals(this.dto.getName(), dto.getName());
    }
}