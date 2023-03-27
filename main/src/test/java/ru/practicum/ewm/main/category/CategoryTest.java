package ru.practicum.ewm.main.category;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import ru.practicum.ewm.main.tools.ObjectWithId;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static ru.practicum.ewm.main.tools.factories.CategoryFactory.createCategory;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class CategoryTest {
    Long id = 1L;

    String name = "categoryName";

    @SuppressWarnings("all")
    @Test
    void equals_withSameObjectWithNullIdAndOtherFields_shouldReturnTrue() {
        Category category = createCategory(null, null);

        assertThat(category.equals(category), is(true));
    }

    @SuppressWarnings("all")
    @Test
    void equals_withNullAndNullIdAndOtherFields_shouldReturnFalse() {
        Category category = createCategory(null, null);

        assertThat(category.equals(null), is(false));
    }

    @SuppressWarnings("all")
    @Test
    void equals_withObjectOfOtherClassWithNotNullIdsAndNullOtherFields_shouldReturnFalse() {
        Category category = createCategory(id, null);
        ObjectWithId otherObject = new ObjectWithId(id);

        assertThat(category.equals(otherObject), is(false));
    }

    @Test
    void equals_withNullIdsAndNotNullEqualOtherFields_shouldReturnFalse() {
        Category category1 = createCategory(null, name);
        Category category2 = createCategory(null, name);

        assertThat(category1.equals(category2), is(false));
    }

    @Test
    void equals_withNotNullEqualIdsAndNotEqualOtherFields_shouldReturnTrue() {
        Category category1 = createCategory(id, null);
        Category category2 = createCategory(id, name);

        assertThat(category1.equals(category2), is(true));
    }

    @ParameterizedTest(name = "Entities with id={0} and id={0}")
    @NullSource
    @ValueSource(longs = {1L})
    void hashCode_withEqualIdsAndNotEqualOtherFields_shouldBeEqual(Long id) {
        Category category1 = createCategory(id, name);
        Category category2 = createCategory(id, null);

        assertThat(category1.hashCode(), equalTo(category2.hashCode()));
    }

    @Test
    void hashCode_ofTwoCategoriesWithNullAndZeroIdsAndNotEqualOtherFields_shouldBeEqual() {
        Category category1 = createCategory(null, name);
        Category category2 = createCategory(0L, null);

        assertThat(category1.hashCode(), equalTo(category2.hashCode()));
    }

    @ParameterizedTest(name = "Entities with id={0}, id2=2")
    @NullSource
    @ValueSource(longs = {1L})
    void hashCode_ofTwoCategoriesWithNotEqualIdsAndEqualOtherFields_shouldNotBeEqual(Long id) {
        Category category1 = createCategory(id, name);
        Category category2 = createCategory(2L, name);

        assertThat(category1.hashCode(), not(equalTo(category2.hashCode())));
    }
}