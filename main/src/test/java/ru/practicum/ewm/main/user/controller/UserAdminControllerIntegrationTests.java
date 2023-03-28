package ru.practicum.ewm.main.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.ClassRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import ru.practicum.ewm.main.controller.ControllerErrorHandler;
import ru.practicum.ewm.main.tools.PostgresqlTestContainer;
import ru.practicum.ewm.main.user.User;
import ru.practicum.ewm.main.user.dto.NewUserRequest;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.ewm.main.tools.factories.UserFactory.createNewUserRequest;
import static ru.practicum.ewm.main.tools.factories.UserFactory.createUser;
import static ru.practicum.ewm.main.tools.matchers.LocalDateTimeMatcher.near;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAdminControllerIntegrationTests {
    @ClassRule
    public static PostgreSQLContainer<PostgresqlTestContainer> postgreSQLContainer =
            PostgresqlTestContainer.getInstance();

    MockMvc mockMvc;

    @Autowired
    UserAdminController controller;

    @Autowired
    EntityManager em;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ControllerErrorHandler controllerErrorHandler;

    static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    final String name = "userName";

    final String email = "user@email.com";

    final NewUserRequest requestUserDto = createNewUserRequest(name, email);

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(controllerErrorHandler)
                .build();
    }

    @Test
    void create_withNotUniqueEmail_shouldReturnStatusConflictAndApiError() throws Exception {
        User userWithSameEmail = createUser(null, "userWithSameEmailName", email);
        em.persist(userWithSameEmail);
        em.flush();

        mockMvc.perform(post("/admin/users")
                        .content(objectMapper.writeValueAsString(requestUserDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isConflict(),
                        jsonPath("$.status", is(HttpStatus.CONFLICT.name())),
                        jsonPath("$.reason", is("Integrity constraint has been violated.")),
                        jsonPath("$.message", is(not(emptyString()))),
                        jsonPath("$.timestamp", is(near(LocalDateTime.now(), DATE_PATTERN)))
                );
    }

    @Test
    void create_shouldReturnStatusCreatedAndDtoOfCreatedUser() throws Exception {
        mockMvc.perform(post("/admin/users")
                        .content(objectMapper.writeValueAsString(requestUserDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.id", is(notNullValue()), Long.class),
                        jsonPath("$.name", is(requestUserDto.getName())),
                        jsonPath("$.email", is(requestUserDto.getEmail()))
                );

        User createdUser = em.createQuery("Select u from User u", User.class).getSingleResult();

        assertThat(createdUser, allOf(
                hasProperty("id", is(notNullValue())),
                hasProperty("name", equalTo(requestUserDto.getName())),
                hasProperty("email", equalTo(requestUserDto.getEmail()))
        ));
    }

    @Test
    void read_shouldReturnDtoListOfSelectedUsers() throws Exception {
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
        em.flush();
        String ids = user1.getId() + "," + user2.getId() + "," + user4.getId() + "," + user5.getId();
        int from = 1;
        int size = 2;

        mockMvc.perform(get("/admin/users?ids=" + ids + "&from=" + from + "&size=" + size))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$[0].id", is(user2.getId()), Long.class),
                        jsonPath("$[0].name", is(user2.getName())),
                        jsonPath("$[0].email", is(user2.getEmail())),
                        jsonPath("$[1].id", is(user4.getId()), Long.class),
                        jsonPath("$[1].name", is(user4.getName())),
                        jsonPath("$[1].email", is(user4.getEmail()))
                );
    }

    @Test
    void read_withNoUsersFound_shouldReturnEmptyDtoList() throws Exception {
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    void delete_withNotExistingId_shouldReturnStatusNotFound() throws Exception {
        long notExistingId = 1000L;
        String message = "User with id = " + notExistingId + " was not found";

        mockMvc.perform(delete("/admin/users/" + notExistingId))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.status", is(HttpStatus.NOT_FOUND.name())),
                        jsonPath("$.reason", is("The required object was not found.")),
                        jsonPath("$.message", is(message)),
                        jsonPath("$.timestamp", is(near(LocalDateTime.now(), DATE_PATTERN)))
                );
    }

    @Test
    void delete_shouldDeleteUserAndReturnStatusOk() throws Exception {
        User existingUser = createUser(null, name, email);
        em.persist(existingUser);
        em.flush();
        Long existingId = existingUser.getId();

        mockMvc.perform(delete("/admin/users/" + existingId))
                .andExpect(status().isNoContent());

        assertThat(em.contains(existingUser), is(false));
    }
}
