package ru.practicum.ewm.main.category.dto;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.ewm.main.category.Category;
import java.util.List;
import java.util.stream.Stream;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.nullValue;
import static ru.practicum.ewm.main.tools.factories.CategoryFactory.*;

@SpringBootTest(classes = {CategoryMapperImpl.class})
@FieldDefaults(level = AccessLevel.PRIVATE)
class CategoryMapperTest {
    @Autowired
    CategoryMapper categoryMapper;

    @Test
    void toDto_withNotNullFields_shouldReturnDtoWithNotNullFields() {
        Category source = createCategory(10L, "categoryName");

        CategoryDto target = categoryMapper.toDto(source);

        assertThat(target, allOf(
                hasProperty("id", equalTo(source.getId())),
                hasProperty("name", equalTo(source.getName()))
        ));
    }

    @Test
    void toDto_withNull_shouldReturnNull() {
        assertThat(categoryMapper.toDto((Category) null), is(nullValue()));
    }

    @Test
    void toDto_withNullFields_shouldReturnDtoWithNullFields() {
        Category source = createCategory(null, null);

        CategoryDto target = categoryMapper.toDto(source);

        assertThat(target, allOf(
                hasProperty("id", is(nullValue())),
                hasProperty("name", is(nullValue()))
        ));
    }

    @Test
    void toDtoList_withNotNull_shouldReturnCategoryDtoList() {
        Category source = createCategory(10L, "categoryName");

        List<CategoryDto> target = categoryMapper.toDto(List.of(source));

        assertThat(target, contains(allOf(
                hasProperty("id", equalTo(source.getId())),
                hasProperty("name", equalTo(source.getName()))
        )));
    }

    @Test
    void toDtoList_withEmptyList_shouldReturnEmptyList() {
        List<CategoryDto> target = categoryMapper.toDto(List.of());

        assertThat(target, is(empty()));
    }

    @Test
    void toDtoList_withNull_shouldReturnNull() {
        List<CategoryDto> target = categoryMapper.toDto((List<Category>) null);

        assertThat(target, is(nullValue()));
    }

    @Test
    void toEntity_withNotNullName_shouldReturnEntityWithNotNullName() {
        NewCategoryDto source = createNewCategoryDto("categoryName");

        Category target = categoryMapper.toEntity(source);

        assertThat(target, allOf(
                hasProperty("id", is(nullValue())),
                hasProperty("name", equalTo(source.getName()))
        ));
    }

    @Test
    void toEntity_withNull_shouldReturnNull() {
        assertThat(categoryMapper.toEntity(null), nullValue());
    }

    @Test
    void toEntity_withNullName_shouldReturnEntityWithNullFields() {
        NewCategoryDto source = createNewCategoryDto(null);

        Category target = categoryMapper.toEntity(source);

        assertThat(target, allOf(
                hasProperty("id", is(nullValue())),
                hasProperty("name", is(nullValue()))
        ));
    }

    static Stream<Arguments> update() {
        return Stream.of(
                Arguments.of(null, null, null),
                Arguments.of(null, null, "oldName"),
                Arguments.of(null, 1L, null),
                Arguments.of(null, 1L, "oldName"),
                Arguments.of(2L, 1L, null),
                Arguments.of(2L, 1L, "oldName")
        );
    }

    @ParameterizedTest(name = "dto:id={0}, entity:id={1}, name={2}=>newName")
    @MethodSource("update")
    void updateEntityFromDto_withNotNullName_shouldUpdateEntityWithNotNullName(Long dtoId, Long entityId, String name) {
        CategoryDto source = createCategoryDto(dtoId, "newName");
        Category target = createCategory(entityId, name);

        categoryMapper.updateEntityFromDto(source, target);

        assertThat(target, allOf(
                hasProperty("id", is(entityId)),
                hasProperty("name", equalTo(source.getName()))
        ));
    }

    static Stream<Arguments> noUpdate() {
        return Stream.of(
                Arguments.of("null dto", null),
                Arguments.of("dto with null id and name", createCategoryDto(null, null)),
                Arguments.of("dto with not null id and null name", createCategoryDto(100L, null))
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("noUpdate")
    void updateEntityFromDto_withNullOrDtoWithNullName_shouldNotUpdateEntity(String testName, CategoryDto source) {
        Long id = 1L;
        String name = "name";
        Category target = createCategory(id, name);

        categoryMapper.updateEntityFromDto(source, target);

        assertThat(target, allOf(
                hasProperty("id", equalTo(id)),
                hasProperty("name", equalTo(name))
        ));
    }
}