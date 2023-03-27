package ru.practicum.ewm.main.tools.matchers;

import org.mockito.ArgumentMatcher;
import ru.practicum.ewm.main.category.dto.NewCategoryDto;
import java.util.Objects;

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
        return dto != null && this.dto != null
                && Objects.equals(this.dto.getName(), dto.getName());
    }
}