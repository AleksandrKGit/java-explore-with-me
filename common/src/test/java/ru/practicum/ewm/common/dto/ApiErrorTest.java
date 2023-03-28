package ru.practicum.ewm.common.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.json.JSONException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static ru.practicum.ewm.common.dto.tools.matchers.LocalDateTimeMatcher.near;
import static ru.practicum.ewm.common.support.DateFactory.*;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class ApiErrorTest {
    ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    static HttpStatus status = HttpStatus.OK;

    static String reason = "reason";

    static List<String> errors = List.of("error1", "error2");

    static Exception exception = new Exception("message");

    private static Stream<Arguments> nullFields() {
        return Stream.of(
                Arguments.of("created with null exception",
                        new ApiError(null, null, (Exception) null)),

                Arguments.of("created with null errors",
                        new ApiError(null, null, (List<String>) null))
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("nullFields")
    void toJson_withNullFields_shouldReturnJsonStringWithNullFields(
            String testName, ApiError source) throws JsonProcessingException, JSONException {
        assertThat(source.getTimestamp(), near(LocalDateTime.now()));

        source.setTimestamp(null);
        String expected = "{\"errors\":null, \"status\":null, \"reason\":null, \"message\":null, \"timestamp\":null}";

        String actual = objectMapper.writeValueAsString(source);

        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    private static Stream<Arguments> notNullFields() {
        return Stream.of(
                Arguments.of("created with not null exception",
                        new ApiError(status, reason, exception),
                        exception.getMessage()),

                Arguments.of("created with not null errors",
                        new ApiError(status, reason, errors),
                        String.join("\r\n", errors))
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("notNullFields")
    void toJson_withNotNullFields_shouldReturnJsonStringWithNotNullFields(
            String testName, ApiError source, String message) throws JsonProcessingException, JSONException {
        assertThat(source.getTimestamp(), is(near(LocalDateTime.now())));

        LocalDateTime timestamp = LocalDateTime.now();
        source.setTimestamp(timestamp);
        source.setErrors(errors);
        String expected = "{\"errors\":[\"error1\", \"error2\"], \"status\":\"OK\", \"reason\":\"reason\", " +
                "\"message\":\"" + message + "\", \"timestamp\":\"" + ofDate(timestamp) + "\"}";

        String actual = objectMapper.writeValueAsString(source);

        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }
}