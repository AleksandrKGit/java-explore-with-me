package ru.practicum.ewm.main.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import ru.practicum.ewm.main.tools.ObjectWithId;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static ru.practicum.ewm.main.tools.factories.UserFactory.createUser;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class UserTest {
    Long id = 1L;

    String email = "user@email.com";

    String name = "UserName";

    @SuppressWarnings("all")
    @Test
    void equals_withSameObjectWithNullIdAndOtherFields_shouldReturnTrue() {
        User user = createUser(null, null, null);

        assertThat(user.equals(user), is(true));
    }

    @SuppressWarnings("all")
    @Test
    void equals_withNullAndNullIdAndOtherFields_shouldReturnFalse() {
        User user = createUser(null, null, null);

        assertThat(user.equals(null), is(false));
    }

    @SuppressWarnings("all")
    @Test
    void equals_withObjectOfOtherClassWithNotNullIdsAndNullOtherFields_shouldReturnFalse() {
        User user = createUser(id, null, null);
        ObjectWithId otherObject = new ObjectWithId(id);

        assertThat(user.equals(otherObject), is(false));
    }

    @Test
    void equals_withNullIdsAndNotNullEqualOtherFields_shouldReturnFalse() {
        User user1 = createUser(null, name, email);
        User user2 = createUser(null, name, email);

        assertThat(user1.equals(user2), is(false));
    }

    @Test
    void equals_withNotNullEqualIdsAndNotEqualOtherFields_shouldReturnTrue() {
        User user1 = createUser(id, null, null);
        User user2 = createUser(id, name, email);

        assertThat(user1.equals(user2), is(true));
    }

    @ParameterizedTest(name = "Entities with id={0} and id={0}")
    @NullSource
    @ValueSource(longs = {1L})
    void hashCode_withEqualIdsAndNotEqualOtherFields_shouldBeEqual(Long id) {
        User user1 = createUser(id, name, email);
        User user2 = createUser(id, null, null);

        assertThat(user1.hashCode(), equalTo(user2.hashCode()));
    }

    @Test
    void hashCode_ofTwoUsersWithNullAndZeroIdsAndNotEqualOtherFields_shouldBeEqual() {
        User user1 = createUser(null, name, email);
        User user2 = createUser(0L, null, null);

        assertThat(user1.hashCode(), equalTo(user2.hashCode()));
    }

    @ParameterizedTest(name = "Entities with id={0}, id2=2")
    @NullSource
    @ValueSource(longs = {1L})
    void hashCode_ofTwoUsersWithNotEqualIdsAndEqualOtherFields_shouldNotBeEqual(Long id) {
        User user1 = createUser(id, name, email);
        User user2 = createUser(2L, name, email);

        assertThat(user1.hashCode(), not(equalTo(user2.hashCode())));
    }
}