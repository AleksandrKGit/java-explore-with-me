package ru.practicum.ewm.main.user.dto;

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
import static ru.practicum.ewm.main.tools.factories.UserFactory.*;

@JsonTest
@SpringJUnitConfig({AppTestConfiguration.class})
@FieldDefaults(level = AccessLevel.PRIVATE)
class NewUserRequestTest {
    @Autowired
    LocalValidatorFactoryBean validator;

    @Autowired
    ObjectMapper objectMapper;

    static final String validName = "userName";
    static final String validEmail = "user@email.com";

    @Test
    void validate_withCorrectFields_shouldReturnEmptyListOfConstraintViolations() {
        Set<ConstraintViolation<NewUserRequest>> target =
                validator.validate(createNewUserRequest(validName, validEmail));

        assertThat(target, is(empty()));
    }

    private static Stream<Arguments> invalidDto() {
        String bigName = Strings.repeat("a", 256);
        String blankName = " \t\r\n   ";
        String bigEmail = Strings.repeat("a", 242) + "email@mail.com";
        String invalidEmail = "email@mail@com";

        return Stream.of(
                Arguments.of("null name",
                        createNewUserRequest(null, validEmail)),

                Arguments.of("null email",
                        createNewUserRequest(validName, null)),

                Arguments.of("empty name",
                        createNewUserRequest("", validEmail)),

                Arguments.of("empty email",
                        createNewUserRequest(validName, "")),

                Arguments.of("big name",
                        createNewUserRequest(bigName, validEmail)),

                Arguments.of("big email",
                        createNewUserRequest(validName, bigEmail)),

                Arguments.of("blank name",
                        createNewUserRequest(blankName, validEmail)),

                Arguments.of("invalid email",
                        createNewUserRequest(validName, invalidEmail))
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidDto")
    void validate_withInvalidFields_shouldReturnNotEmptyListOfConstraintViolations(
            String testName, NewUserRequest dto) {
        Set<ConstraintViolation<NewUserRequest>> target = validator.validate(dto);

        assertThat(target, is(not(empty())));
    }

    @Test
    void fromJson_withNotEmptyFields_shouldReturnDtoWithNotEmptyFields() throws JsonProcessingException {
        NewUserRequest target = objectMapper.readValue(String.format("{\"name\":\"%s\", \"email\":\"%s\"}", validName,
                validEmail), NewUserRequest.class);

        assertThat(target, allOf(
                hasProperty("name", equalTo(validName)),
                hasProperty("email", equalTo(validEmail))
        ));
    }

    @Test
    void fromJson_withEmptyFields_shouldReturnDtoWithEmptyFields() throws JsonProcessingException {
        NewUserRequest target = objectMapper.readValue("{\"name\":\"\", \"email\":\"\"}",
                NewUserRequest.class);

        assertThat(target, allOf(
                hasProperty("name", is(emptyString())),
                hasProperty("email", is(emptyString()))
        ));
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", "{\"name\":null, \"email\":null}"})
    void fromJson_withNoOrNullFields_shouldReturnDtoWithNullFields(String source) throws JsonProcessingException {
        NewUserRequest target = objectMapper.readValue(source, NewUserRequest.class);

        assertThat(target, allOf(
                hasProperty("name", is(nullValue())),
                hasProperty("email", is(nullValue()))
        ));
    }
}