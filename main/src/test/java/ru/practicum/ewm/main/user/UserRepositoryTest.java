package ru.practicum.ewm.main.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.logging.log4j.util.Strings;
import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.testcontainers.containers.PostgreSQLContainer;
import ru.practicum.ewm.main.support.OffsetPageRequest;
import ru.practicum.ewm.main.tools.PostgresqlTestContainer;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Stream;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.ewm.main.tools.factories.UserFactory.createUser;

@Transactional
@DataJpaTest
@FieldDefaults(level = AccessLevel.PRIVATE)
class UserRepositoryTest {
    @ClassRule
    public static PostgreSQLContainer<PostgresqlTestContainer> postgreSQLContainer =
            PostgresqlTestContainer.getInstance();

    @Autowired
    TestEntityManager em;

    @Autowired
    UserRepository repository;

    static final Long notExistingId = 1000L;

    static final String name = "userName";

    static final String email = "user@email.com";

    final Integer from = 1;

    final Integer size = 2;

    final Sort sort = Sort.by("id").ascending();

    private static Stream<Arguments> save() {
        return Stream.of(
                Arguments.of("", ""),
                Arguments.of(name, email)
        );
    }

    @ParameterizedTest(name = "name={0}, email={1}")
    @MethodSource("save")
    void save_withWithNullId_shouldReturnAttachedTransferredUserWithGeneratedId(String name, String email) {
        User user = createUser(null, name, email);

        User savedUser = repository.saveAndFlush(user);

        assertThat(savedUser == user, is(true));
        assertThat(savedUser, hasProperty("id", is(not(nullValue()))));
    }

    @ParameterizedTest(name = "name={0}, email={1}")
    @MethodSource("save")
    void save_withWithNotExistingId_shouldReturnNewAttachedUserWithGeneratedId(String name, String email) {
        User user = createUser(notExistingId, name, email);

        User savedUser = repository.saveAndFlush(user);

        assertThat(user, hasProperty("id", is(notNullValue())));
        assertThat(savedUser == user, is(false));
        assertThat(savedUser, allOf(
                hasProperty("id", not(nullValue())),
                hasProperty("id", not(equalTo(user.getId()))),
                hasProperty("name", equalTo(user.getName())),
                hasProperty("email", equalTo(user.getEmail()))
        ));
    }

    private static Stream<Arguments> incorrectFields() {
        return Stream.of(
                Arguments.of("null name",
                        null, email),

                Arguments.of("big name",
                        Strings.repeat("n", 256), email),

                Arguments.of("null email",
                        name, null),

                Arguments.of("big email",
                        name, Strings.repeat("n", 256))
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("incorrectFields")
    void save_withIncorrectFields_shouldThrowException(String testName, String name, String email) {
        User user = createUser(null, name, email);

        assertThrows(Exception.class, () -> repository.saveAndFlush(user));
    }

    @Test
    void save_withNotUniqueEmail_shouldThrowDataIntegrityViolationException() {
        User existingUser = createUser(null, "existingUserName", email);
        em.persist(existingUser);
        User user = createUser(null, name, email);

        assertThrows(DataIntegrityViolationException.class, () -> repository.saveAndFlush(user));
    }

    @Test
    void delete_withNotExistingId_shouldThrowEmptyResultDataAccessException() {
        assertThrows(EmptyResultDataAccessException.class, () -> repository.deleteById(notExistingId));
    }

    @Test
    void delete_shouldRemoveUserWithSuchId() {
        User existingUser = createUser(null, name, email);
        em.persist(existingUser);
        em.flush();
        Long existingId = existingUser.getId();

        repository.deleteById(existingId);

        assertThat(repository.findById(existingId).isEmpty(), is(true));
    }

    @Test
    void findAll_withNoUsers_shouldReturnEmptyPage() {
        Pageable pageRequest = OffsetPageRequest.ofOffset(0, 10, sort);

        Page<User> result = repository.findAll(pageRequest);

        assertThat(result, is(emptyIterable()));
    }

    @Test
    void findAll_withOffsetPageRequest_shouldReturnPageWithUsers() {
        Pageable pageRequest = OffsetPageRequest.ofOffset(from, size, sort);

        User user1 = createUser(null, "n1", "e1@email.com");
        User user2 = createUser(null, "n2", "e2@email.com");
        User user3 = createUser(null, "n3", "e3@email.com");
        User user4 = createUser(null, "n4", "e4@email.com");
        em.persist(user1);
        em.persist(user2);
        em.persist(user3);
        em.persist(user4);

        Page<User> result = repository.findAll(pageRequest);

        assertThat(result, contains(
                user2,
                user3
        ));
    }

    @Test
    void findByIdIn_shouldReturnPageWithUsersWithSelectedIds() {
        Pageable pageRequest = OffsetPageRequest.ofOffset(from, size, sort);

        User user1 = createUser(null, "n1", "e1@email.com");
        User user2 = createUser(null, "n2", "e2@email.com");
        User user3 = createUser(null, "n3", "e3@email.com");
        User user4 = createUser(null, "n4", "e4@email.com");
        User user5 = createUser(null, "n5", "e5@email.com");
        em.persist(user1);
        em.persist(user2);
        em.persist(user3);
        em.persist(user4);
        em.persist(user5);

        Page<User> result = repository.findByIdIn(List.of(user1.getId(), user2.getId(), user4.getId(), user5.getId()),
                pageRequest);

        assertThat(result, contains(
                user2,
                user4
        ));
    }
}