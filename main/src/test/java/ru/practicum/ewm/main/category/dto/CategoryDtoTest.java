package ru.practicum.ewm.main.category.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.logging.log4j.util.Strings;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import ru.practicum.ewm.main.tools.configuration.AppTestConfiguration;
import javax.validation.ConstraintViolation;
import java.util.Set;
import java.util.stream.Stream;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static ru.practicum.ewm.main.tools.factories.CategoryFactory.*;

@JsonTest
@SpringJUnitConfig({AppTestConfiguration.class})
@FieldDefaults(level = AccessLevel.PRIVATE)
class CategoryDtoTest {
    @Autowired
    LocalValidatorFactoryBean validator;

    @Autowired
    ObjectMapper objectMapper;

    static final Long validId = 10L;

    static final String validName = "categoryName";

    @Test
    void validate_withCorrectFields_shouldReturnEmptyListOfConstraintViolations() {
        Set<ConstraintViolation<CategoryDto>> target = validator.validate(createCategoryDto(validId, validName));

        assertThat(target, is(empty()));
    }

    private static Stream<Arguments> invalidName() {
        return Stream.of(
                Arguments.of("null name", null),
                Arguments.of("empty name", ""),
                Arguments.of("big name", Strings.repeat("a", 256)),
                Arguments.of("blank name", " \t\r\n   ")
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidName")
    void validate_withInvalidName_shouldReturnNotEmptyListOfConstraintViolations(String testName, String name) {
        Set<ConstraintViolation<CategoryDto>> target = validator.validate(createCategoryDto(validId, name));

        assertThat(target, is(not(empty())));
    }

    @Test
    void fromJson_withNotEmptyFields_shouldReturnDtoWithNotEmptyFields() throws JsonProcessingException {
        CategoryDto target = objectMapper.readValue(String.format("{\"name\":\"%s\", \"id\":%s}", validName,
                validId), CategoryDto.class);

        assertThat(target, allOf(
                hasProperty("name", equalTo(validName)),
                hasProperty("id", equalTo(validId))
        ));
    }

    @Test
    void fromJson_withEmptyName_shouldReturnDtoWithEmptyName() throws JsonProcessingException {
        CategoryDto target = objectMapper.readValue("{\"name\":\"\"}", CategoryDto.class);

        assertThat(target, allOf(
                hasProperty("name", is(emptyString()))
        ));
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", "{\"name\":null, \"id\":null}"})
    void fromJson_withNoOrNullFields_shouldReturnDtoWithNullFields(String source) throws JsonProcessingException {
        CategoryDto target = objectMapper.readValue(source, CategoryDto.class);

        assertThat(target, allOf(
                hasProperty("name", is(nullValue())),
                hasProperty("id", is(nullValue()))
        ));
    }

    @Test
    void toJson_withNullFields_shouldReturnJsonStringWithNullFields() throws JsonProcessingException, JSONException {
        CategoryDto source = createCategoryDto(null, null);
        String expected = "{\"id\":null, \"name\":null}";

        String actual = objectMapper.writeValueAsString(source);

        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void toJson_withNotNullFields_shouldReturnCorrectJsonString() throws JsonProcessingException, JSONException {
        CategoryDto source = createCategoryDto(1L,"category");
        String expected = "{\"id\":1, \"name\":\"category\"}";

        String actual = objectMapper.writeValueAsString(source);

        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }
}