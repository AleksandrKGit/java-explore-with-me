package ru.practicum.ewm.main.tools.matchers;

import org.mockito.ArgumentMatcher;
import ru.practicum.ewm.main.category.Category;
import ru.practicum.ewm.main.tools.factories.CategoryFactory;

public class CategoryMatcher implements ArgumentMatcher<Category> {
    private final Category category;

    private CategoryMatcher(Category category) {
        this.category = category;
    }

    public static CategoryMatcher equalTo(Category category) {
        return new CategoryMatcher(category);
    }

    @Override
    public boolean matches(Category category) {
        return CategoryFactory.equals(this.category, category);
    }
}