package ru.practicum.ewm.main.tools.matchers;

import org.mockito.ArgumentMatcher;
import ru.practicum.ewm.main.category.dto.NewCategoryDto;
import ru.practicum.ewm.main.tools.factories.CategoryFactory;

public class NewCategoryDtoMatcher implements ArgumentMatcher<NewCategoryDto> {
    private final NewCategoryDto dto;

    private NewCategoryDtoMatcher(NewCategoryDto dto) {
        this.dto = dto;
    }

    public static NewCategoryDtoMatcher equalTo(NewCategoryDto dto) {
        return new NewCategoryDtoMatcher(dto);
    }

    @Override
    public boolean matches(NewCategoryDto dto) {
        return CategoryFactory.equals(this.dto, dto);
    }
}