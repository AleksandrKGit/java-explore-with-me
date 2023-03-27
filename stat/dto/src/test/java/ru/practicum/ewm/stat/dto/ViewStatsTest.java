package ru.practicum.ewm.stat.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class ViewStatsTest {
    ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void fromJson_withNotEmptyFields_shouldReturnDtoWithNotEmptyFields() throws JsonProcessingException {
        String validApp = "itemApp";
        String validUri = "https://ya.ru";
        Long validHits = 5L;
        ViewStats target = objectMapper.readValue("{\"app\":\"" + validApp + "\", \"uri\":\"" + validUri
                + "\", \"hits\":" + validHits + "}", ViewStats.class);

        assertThat(target, allOf(
                hasProperty("app", equalTo(validApp)),
                hasProperty("uri", equalTo(validUri)),
                hasProperty("hits", equalTo(validHits))
        ));
    }

    @Test
    void fromJson_withEmptyFields_shouldReturnDtoWithEmptyFields() throws JsonProcessingException {
        ViewStats target = objectMapper.readValue("{\"app\":\"\", \"uri\":\"\"}", ViewStats.class);

        assertThat(target, allOf(
                hasProperty("app", is(emptyString())),
                hasProperty("uri", is(emptyString()))
        ));
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", "{\"app\":null, \"uri\":null, \"hits\":null}"})
    void fromJson_withNoOrNullFields_shouldReturnDtoWithNullFields(String source) throws JsonProcessingException {
        ViewStats target = objectMapper.readValue(source, ViewStats.class);

        assertThat(target, allOf(
                hasProperty("app", is(nullValue())),
                hasProperty("uri", is(nullValue())),
                hasProperty("hits", is(nullValue()))
        ));
    }
}