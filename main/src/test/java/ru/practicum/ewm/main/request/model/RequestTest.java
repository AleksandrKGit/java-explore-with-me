package ru.practicum.ewm.main.request.model;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import ru.practicum.ewm.main.event.model.Event;
import ru.practicum.ewm.main.event.model.EventState;
import ru.practicum.ewm.main.tools.ObjectWithId;
import ru.practicum.ewm.main.user.User;
import java.time.LocalDateTime;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static ru.practicum.ewm.main.tools.factories.CategoryFactory.createCategory;
import static ru.practicum.ewm.main.tools.factories.EventFactory.createEvent;
import static ru.practicum.ewm.main.tools.factories.EventFactory.createLocation;
import static ru.practicum.ewm.main.tools.factories.RequestFactory.createRequest;
import static ru.practicum.ewm.main.tools.factories.UserFactory.createUser;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class RequestTest {
    Long id = 1L;

    User requestor = createUser(2L, "userName", "user@email.com");

    Event event = createEvent(3L, createUser(4L, "initiatorName", "initiator@email.com"),
            createCategory(5L, "categoryName"), "eventTitle", "eventAnnotation",
            "eventDescription", 10, createLocation(1.1f, 2.2f), true,
            true, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1),
            LocalDateTime.now().plusDays(1), EventState.PUBLISHED);

    LocalDateTime created = LocalDateTime.now();

    RequestStatus status = RequestStatus.CONFIRMED;

    @SuppressWarnings("all")
    @Test
    void equals_withSameObjectWithNullIdAndOtherFields_shouldReturnTrue() {
        Request request = createRequest(null, null, null, null, null);

        assertThat(request.equals(request), is(true));
    }

    @SuppressWarnings("all")
    @Test
    void equals_withNullAndNullIdAndOtherFields_shouldReturnFalse() {
        Request request = createRequest(null, null, null, null, null);

        assertThat(request.equals(null), is(false));
    }

    @SuppressWarnings("all")
    @Test
    void equals_withObjectOfOtherClassWithNotNullIdsAndNullOtherFields_shouldReturnFalse() {
        Request request = createRequest(id, null, null, null, null);
        ObjectWithId otherObject = new ObjectWithId(id);

        assertThat(request.equals(otherObject), is(false));
    }

    @Test
    void equals_withNullIdsAndNotNullEqualOtherFields_shouldReturnFalse() {
        Request request1 = createRequest(null, requestor, event, created, status);
        Request request2 = createRequest(null, requestor, event, created, status);

        assertThat(request1.equals(request2), is(false));
    }

    @Test
    void equals_withNotNullEqualIdsAndNotEqualOtherFields_shouldReturnTrue() {
        Request request1 = createRequest(id, null, null, null, null);
        Request request2 = createRequest(id, requestor, event, created, status);

        assertThat(request1.equals(request2), is(true));
    }

    @ParameterizedTest(name = "Entities with id={0} and id={0}")
    @NullSource
    @ValueSource(longs = {1L})
    void hashCode_withEqualIdsAndNotEqualOtherFields_shouldBeEqual(Long id) {
        Request request1 = createRequest(id, requestor, event, created, status);
        Request request2 = createRequest(id, null, null, null, null);

        assertThat(request1.hashCode(), equalTo(request2.hashCode()));
    }

    @Test
    void hashCode_ofTwoUsersWithNullAndZeroIdsAndNotEqualOtherFields_shouldBeEqual() {
        Request request1 = createRequest(null, requestor, event, created, status);
        Request request2 = createRequest(0L, null, null, null, null);

        assertThat(request1.hashCode(), equalTo(request2.hashCode()));
    }

    @ParameterizedTest(name = "Entities with id={0}, id2=2")
    @NullSource
    @ValueSource(longs = {1L})
    void hashCode_ofTwoUsersWithNotEqualIdsAndEqualOtherFields_shouldNotBeEqual(Long id) {
        Request request1 = createRequest(id, requestor, event, created, status);
        Request request2 = createRequest(2L, requestor, event, created, status);

        assertThat(request1.hashCode(), not(equalTo(request2.hashCode())));
    }
}