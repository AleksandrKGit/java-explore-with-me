package ru.practicum.ewm.main.exception;

import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class NotFoundExceptionTest {
    @Test
    void constructor_withNullMessage_shouldCreateExceptionWithNullMessage() {
        NotFoundException exception = new NotFoundException(null);

        assertThat(exception, hasProperty("message", is(nullValue())));
    }

    @Test
    void constructor_withNotNullMessage_shouldCreateExceptionWithNotNullMessage() {
        String message = "message";
        NotFoundException exception = new NotFoundException(message);

        assertThat(exception, hasProperty("message", equalTo(message)));
    }
}