package ru.practicum.ewm.main.event.model;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import ru.practicum.ewm.main.category.Category;
import ru.practicum.ewm.main.tools.ObjectWithId;
import ru.practicum.ewm.main.user.User;
import java.time.LocalDateTime;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static ru.practicum.ewm.main.tools.factories.CategoryFactory.createCategory;
import static ru.practicum.ewm.main.tools.factories.EventFactory.createEvent;
import static ru.practicum.ewm.main.tools.factories.EventFactory.createLocation;
import static ru.practicum.ewm.main.tools.factories.UserFactory.createUser;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class EventTest {
    Long id = 1L;

    User initiator = createUser(2L, "userName", "user@email.com");

    Category category = createCategory(3L, "categoryName");

    String title = "eventTitle";

    String annotation = "eventAnnotation";

    String description = "eventDescription";

    Integer participantLimit = 10;

    Location location = createLocation(1.1f, 2.2f);

    Boolean requestModeration = true;

    Boolean paid = true;

    LocalDateTime createdOn = LocalDateTime.now().minusDays(1);

    LocalDateTime publishedOn = LocalDateTime.now();

    LocalDateTime eventDate = LocalDateTime.now().plusDays(1);

    EventState state = EventState.PUBLISHED;

    @SuppressWarnings("all")
    @Test
    void equals_withSameObjectWithNullIdAndOtherFields_shouldReturnTrue() {
        Event event = createEvent(null, null, null, null, null, null,
                null, null, null, null, null, null,
                null, null);

        assertThat(event.equals(event), is(true));
    }

    @SuppressWarnings("all")
    @Test
    void equals_withNullAndNullIdAndOtherFields_shouldReturnFalse() {
        Event event = createEvent(null, null, null, null, null, null,
                null, null, null, null, null, null,
                null, null);

        assertThat(event.equals(null), is(false));
    }

    @SuppressWarnings("all")
    @Test
    void equals_withObjectOfOtherClassWithNotNullIdsAndNullOtherFields_shouldReturnFalse() {
        Event event = createEvent(id, null, null, null, null, null,
                null, null, null, null, null, null,
                null, null);
        ObjectWithId otherObject = new ObjectWithId(id);

        assertThat(event.equals(otherObject), is(false));
    }

    @Test
    void equals_withNullIdsAndNotNullEqualOtherFields_shouldReturnFalse() {
        Event event1 = createEvent(null, initiator, category, title, annotation, description, participantLimit,
                location, requestModeration, paid, createdOn, publishedOn, eventDate, state);
        Event event2 = createEvent(null, initiator, category, title, annotation, description, participantLimit,
                location, requestModeration, paid, createdOn, publishedOn, eventDate, state);

        assertThat(event1.equals(event2), is(false));
    }

    @Test
    void equals_withNotNullEqualIdsAndNotEqualOtherFields_shouldReturnTrue() {
        Event event1 = createEvent(id, null, null, null, null, null,
                null, null, null, null, null, null,
                null, null);
        Event event2 = createEvent(id, initiator, category, title, annotation, description, participantLimit,
                location, requestModeration, paid, createdOn, publishedOn, eventDate, state);

        assertThat(event1.equals(event2), is(true));
    }

    @ParameterizedTest(name = "Entities with id={0} and id={0}")
    @NullSource
    @ValueSource(longs = {1L})
    void hashCode_withEqualIdsAndNotEqualOtherFields_shouldBeEqual(Long id) {
        Event event1 = createEvent(id, initiator, category, title, annotation, description, participantLimit,
                location, requestModeration, paid, createdOn, publishedOn, eventDate, state);
        Event event2 = createEvent(id, null, null, null, null, null,
                null, null, null, null, null, null,
                null, null);

        assertThat(event1.hashCode(), equalTo(event2.hashCode()));
    }

    @Test
    void hashCode_ofTwoUsersWithNullAndZeroIdsAndNotEqualOtherFields_shouldBeEqual() {
        Event event1 = createEvent(null, initiator, category, title, annotation, description, participantLimit,
                location, requestModeration, paid, createdOn, publishedOn, eventDate, state);
        Event event2 = createEvent(0L, null, null, null, null, null,
                null, null, null, null, null, null,
                null, null);

        assertThat(event1.hashCode(), equalTo(event2.hashCode()));
    }

    @ParameterizedTest(name = "Entities with id={0}, id2=2")
    @NullSource
    @ValueSource(longs = {1L})
    void hashCode_ofTwoUsersWithNotEqualIdsAndEqualOtherFields_shouldNotBeEqual(Long id) {
        Event event1 = createEvent(id, initiator, category, title, annotation, description, participantLimit,
                location, requestModeration, paid, createdOn, publishedOn, eventDate, state);
        Event event2 = createEvent(2L, initiator, category, title, annotation, description, participantLimit,
                location, requestModeration, paid, createdOn, publishedOn, eventDate, state);

        assertThat(event1.hashCode(), not(equalTo(event2.hashCode())));
    }
}