package ru.practicum.ewm.stat.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class EndpointHitTest {
    ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void fromJson_withNotEmptyFields_shouldReturnDtoWithNotEmptyFields() throws JsonProcessingException {
        String validApp = "itemApp";
        String validUri = "https://ya.ru";
        String validIp = "192.169.0.1";
        String validTimestamp = "2023-03-01 00:00:00";
        EndpointHit target = objectMapper.readValue(
                String.format("{\"app\":\"%s\", \"uri\":\"%s\", \"ip\":\"%s\", \"timestamp\":\"%s\"}",
                        validApp, validUri, validIp, validTimestamp), EndpointHit.class);

        assertThat(target, allOf(
                hasProperty("app", equalTo(validApp)),
                hasProperty("uri", equalTo(validUri)),
                hasProperty("ip", equalTo(validIp)),
                hasProperty("timestamp", equalTo(validTimestamp))
        ));
    }

    @Test
    void fromJson_withEmptyFields_shouldReturnDtoWithEmptyFields() throws JsonProcessingException {
        EndpointHit target = objectMapper.readValue("{\"app\":\"\", \"uri\":\"\", \"ip\":\"\", "
                        + "\"timestamp\":\"\"}", EndpointHit.class);

        assertThat(target, allOf(
                hasProperty("app", is(emptyString())),
                hasProperty("uri", is(emptyString())),
                hasProperty("ip", is(emptyString())),
                hasProperty("timestamp", is(emptyString()))
        ));
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", "{\"app\":null, \"uri\":null, \"ip\":null, \"timestamp\":null}"})
    void fromJson_withNoOrNullFields_shouldReturnDtoWithNullFields(String source) throws JsonProcessingException {
        EndpointHit target = objectMapper.readValue(source, EndpointHit.class);

        assertThat(target, allOf(
                hasProperty("app", is(nullValue())),
                hasProperty("uri", is(nullValue())),
                hasProperty("ip", is(nullValue())),
                hasProperty("timestamp", is(nullValue()))
        ));
    }
}