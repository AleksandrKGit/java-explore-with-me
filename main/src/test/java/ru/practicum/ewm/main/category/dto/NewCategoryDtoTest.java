package ru.practicum.ewm.main.category.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
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
import static ru.practicum.ewm.main.tools.factories.CategoryFactory.createNewCategoryDto;

@JsonTest
@SpringJUnitConfig({AppTestConfiguration.class})
@FieldDefaults(level = AccessLevel.PRIVATE)
class NewCategoryDtoTest {
    @Autowired
    LocalValidatorFactoryBean validator;

    @Autowired
    ObjectMapper objectMapper;

    static final String validName = "categoryName";

    @Test
    void validate_withCorrectFields_shouldReturnEmptyListOfConstraintViolations() {
        Set<ConstraintViolation<NewCategoryDto>> target = validator.validate(createNewCategoryDto(validName));

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
        Set<ConstraintViolation<NewCategoryDto>> target = validator.validate(createNewCategoryDto(name));

        assertThat(target, is(not(empty())));
    }

    @Test
    void fromJson_withNotEmptyName_shouldReturnDtoWithNotEmptyName() throws JsonProcessingException {
        NewCategoryDto target = objectMapper.readValue(String.format("{\"name\":\"%s\"}", validName),
                NewCategoryDto.class);

        assertThat(target, hasProperty("name", equalTo(validName)));
    }

    @Test
    void fromJson_withEmptyName_shouldReturnDtoWithEmptyName() throws JsonProcessingException {
        NewCategoryDto target = objectMapper.readValue("{\"name\":\"\"}", NewCategoryDto.class);

        assertThat(target, hasProperty("name", is(emptyString())));
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", "{\"name\":null}"})
    void fromJson_withNoOrNullName_shouldReturnDtoWithNullName(String source) throws JsonProcessingException {
        NewCategoryDto target = objectMapper.readValue(source, NewCategoryDto.class);

        assertThat(target, hasProperty("name", is(nullValue())));
    }
}