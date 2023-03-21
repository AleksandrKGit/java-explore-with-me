package ru.practicum.ewm.stat.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class QueryParametersTest {
    private QueryParameters params;

    @BeforeEach
    void beforeEach() {
        params = new QueryParameters();
    }

    @Test
    void getParameters_withNoParameters_shouldReturnNull() {
        assertThat(params.getParameters(), is(nullValue()));
    }

    @Test
    void getQuery_withNoParameters_shouldEmptyString() {
        assertThat(params.getQuery(), is(emptyString()));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = "value")
    void add_withNullNameAndNotNullValue_shouldThrowNullPointerException(String value) {
        assertThrows(NullPointerException.class, () -> params.add(null, value));
    }

    @Test
    void add_withNullValue_shouldNotAddParameter() {
        params.add("name", null);

        assertThat(params.getParameters(), is(nullValue()));
        assertThat(params.getQuery(), is(emptyString()));
    }

    @Test
    void add_withNotNullNameAndValueToEmptyParams_shouldAddParameterToMapAndQueryStartingWithQuestionMark() {
        params.add("name", "value");

        assertThat(params.getParameters(), allOf(
                aMapWithSize(1),
                hasEntry("name", "value")
        ));

        assertThat(params.getQuery(), is("?name={name}"));
    }

    @Test
    void add_withNotNullNameAndValueToNotEmptyParams_shouldAddParameterToMapAndQueryWithAmpersand() {
        params.add("name1", "value1");
        params.add("name2", "value2");

        assertThat(params.getParameters(), allOf(
                aMapWithSize(2),
                hasEntry("name1", "value1"),
                hasEntry("name2", "value2")
        ));

        assertThat(params.getQuery(), is("?name1={name1}&name2={name2}"));
    }
}