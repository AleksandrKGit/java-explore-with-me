package ru.practicum.ewm.main.exception;

import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class ForbiddenExceptionTest {
    @Test
    void constructor_withNullMessage_shouldCreateExceptionWithNullMessage() {
        ForbiddenException exception = new ForbiddenException(null);

        assertThat(exception, hasProperty("message", is(nullValue())));
    }

    @Test
    void constructor_withNotNullMessage_shouldCreateExceptionWithNotNullMessage() {
        String message = "message";
        ForbiddenException exception = new ForbiddenException(message);

        assertThat(exception, hasProperty("message", equalTo(message)));
    }
}