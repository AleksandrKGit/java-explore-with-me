package ru.practicum.ewm.main.exception;

import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class ConflictExceptionTest {
    @Test
    void constructor_withNullMessage_shouldCreateExceptionWithNullMessage() {
        ConflictException exception = new ConflictException(null);

        assertThat(exception, hasProperty("message", is(nullValue())));
    }

    @Test
    void constructor_withNotNullMessage_shouldCreateExceptionWithNotNullMessage() {
        String message = "message";
        ConflictException exception = new ConflictException(message);

        assertThat(exception, hasProperty("message", equalTo(message)));
    }
}