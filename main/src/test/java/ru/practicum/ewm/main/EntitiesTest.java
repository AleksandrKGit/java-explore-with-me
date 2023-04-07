package ru.practicum.ewm.main;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.practicum.ewm.main.event.model.EventState;
import ru.practicum.ewm.main.request.model.RequestStatus;
import ru.practicum.ewm.main.tools.ObjectWithId;
import ru.practicum.ewm.main.tools.factories.*;
import static ru.practicum.ewm.common.support.DateFactory.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;

public class EntitiesTest {
    private final Long id = 1L;

    private static Stream<Arguments> getFactory() {
        return Stream.of(
                Arguments.of("User", (EntityFactory) (id, nullOtherFields) -> nullOtherFields
                        ? UserFactory.createUser(id, null, null)
                        : UserFactory.createUser(id, "name", "email@email.com")),

                Arguments.of("Category", (EntityFactory) (id, nullOtherFields) -> nullOtherFields
                        ? CategoryFactory.createCategory(id, null)
                        : CategoryFactory.createCategory(id, "name")),

                Arguments.of("Compilation", (EntityFactory) (id, nullOtherFields) -> nullOtherFields
                        ? CompilationFactory.createCompilation(id, null, null, null)
                        : CompilationFactory.createCompilation(id, "name", true, List.of())),

                Arguments.of("Request", (EntityFactory) (id, nullOtherFields) -> nullOtherFields
                        ? RequestFactory.createRequest(id, null, null, null, null)
                        : RequestFactory.createRequest(id, UserFactory.createUser(10L, null, null),
                        EventFactory.createEvent(20L, null, null, null, null,
                                null, null, null, null, null,
                                null, null, null, null, null,
                                null), now(), RequestStatus.CONFIRMED)),

                Arguments.of("Event", (EntityFactory) (id, nullOtherFields) -> nullOtherFields
                        ? EventFactory.createEvent(id, null, null, null, null,
                        null, null, null, null, null, null,
                        null, null, null, null,null)
                        : EventFactory.createEvent(id, UserFactory.createUser(10L, null, null),
                        CategoryFactory.createCategory(20L, null), "title", "annotation",
                        "description", EventFactory.createLocation(1.0f, 2.0f), true,
                        true, 15, now(), now(), now(), EventState.PUBLISHED,
                        8L, List.of()))
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("getFactory")
    @SuppressWarnings("all")
    void equals_withSameObjectWithNullIdAndOtherFields_shouldReturnTrue(String testName, EntityFactory factory) {
        Object entity = factory.create(null, true);

        assertThat(entity.equals(entity), is(true));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("getFactory")
    @SuppressWarnings("all")
    void equals_withNullAndNullIdAndOtherFields_shouldReturnFalse(String testName, EntityFactory factory) {
        Object entity = factory.create(null, true);

        assertThat(entity.equals(null), is(false));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("getFactory")
    @SuppressWarnings("all")
    void equals_withObjectOfOtherClassWithNotNullIdsAndNullOtherFields_shouldReturnFalse(String testName,
                                                                                         EntityFactory factory) {
        Object entity = factory.create(id, true);
        ObjectWithId otherObject = new ObjectWithId(id);

        assertThat(entity.equals(otherObject), is(false));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("getFactory")
    void equals_withNullIdsAndNotNullEqualOtherFields_shouldReturnFalse(String testName, EntityFactory factory) {
        Object entity1 = factory.create(null, false);
        Object entity2 = factory.create(null, false);

        assertThat(entity1.equals(entity2), is(false));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("getFactory")
    void equals_withNotNullEqualIdsAndNotEqualOtherFields_shouldReturnTrue(String testName, EntityFactory factory) {
        Object entity1 = factory.create(id, true);
        Object entity2 = factory.create(id, false);

        assertThat(entity1.equals(entity2), is(true));
    }

    private static Stream<Arguments> equalFactory() {
        return withFactory(Stream.of(
                Arguments.of(": objects with null ids", null),
                Arguments.of(": objects with not null ids", 1L)
        ));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("equalFactory")
    void hashCode_withEqualIdsAndNotEqualOtherFields_shouldBeEqual(String testName, EntityFactory factory, Long id) {
        Object entity1 = factory.create(id, false);
        Object entity2 = factory.create(id, true);

        assertThat(entity1.hashCode(), equalTo(entity2.hashCode()));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("getFactory")
    void hashCode_ofTwoUsersWithNullAndZeroIdsAndNotEqualOtherFields_shouldBeEqual(String testName,
                                                                                   EntityFactory factory) {
        Object entity1 = factory.create(null, false);
        Object entity2 = factory.create(0L, true);

        assertThat(entity1.hashCode(), equalTo(entity2.hashCode()));
    }

    private static Stream<Arguments> notEqualFactory() {
        return withFactory(Stream.of(
                Arguments.of(": objects with id = null and id = 2", null, 2L),
                Arguments.of(": objects with id = 1 and id = 2", 1L, 2L)
        ));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("notEqualFactory")
    void hashCode_ofTwoUsersWithNotEqualIdsAndEqualOtherFields_shouldNotBeEqual(String testName, EntityFactory factory,
                                                                                Long id1, Long id2) {
        Object entity1 = factory.create(id1, false);
        Object entity2 = factory.create(id2, false);

        assertThat(entity1.hashCode(), not(equalTo(entity2.hashCode())));
    }

    @FunctionalInterface
    private interface EntityFactory {
        Object create(Long id, boolean nullOtherFields);
    }

    private static Stream<Arguments> withFactory(Stream<Arguments> argsStream) {
        return argsStream.flatMap(
                args1 -> getFactory().map(
                        args2 -> {
                            List<Object> args = new LinkedList<>(Arrays.asList(args1.get()));

                            args.set(0, args2.get()[0].toString() + args.get(0).toString());
                            args.add(1, args2.get()[1]);

                            return Arguments.of(args.toArray());
                        }
                ));
    }
}
