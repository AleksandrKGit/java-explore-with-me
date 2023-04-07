package ru.practicum.ewm.main.category.controller;

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
import ru.practicum.ewm.main.category.dto.CategoryDto;
import ru.practicum.ewm.main.controller.ControllerErrorHandler;
import ru.practicum.ewm.main.category.Category;
import ru.practicum.ewm.main.category.dto.NewCategoryDto;
import ru.practicum.ewm.main.tools.PostgresqlTestContainer;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.ewm.main.tools.factories.CategoryFactory.*;
import static ru.practicum.ewm.main.tools.matchers.LocalDateTimeMatcher.near;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryAdminControllerIntegrationTests {
    @ClassRule
    public static PostgreSQLContainer<PostgresqlTestContainer> postgreSQLContainer =
            PostgresqlTestContainer.getInstance();

    MockMvc mockMvc;

    @Autowired
    CategoryAdminController controller;

    @Autowired
    EntityManager em;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ControllerErrorHandler controllerErrorHandler;

    static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    final String name = "categoryName";

    final NewCategoryDto requestNewCategoryDto = createNewCategoryDto(name);

    final CategoryDto requestUpdateCategoryDto = createCategoryDto(null, name);

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(controllerErrorHandler)
                .build();
    }

    @Test
    void create_withNotUniqueName_shouldReturnStatusConflictAndApiError() throws Exception {
        Category categoryWithSameName = createCategory(null, name);
        em.persist(categoryWithSameName);
        em.flush();

        mockMvc.perform(post("/admin/categories")
                        .content(objectMapper.writeValueAsString(requestNewCategoryDto))
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
    void create_shouldReturnStatusCreatedAndDtoOfCreatedCategory() throws Exception {
        mockMvc.perform(post("/admin/categories")
                        .content(objectMapper.writeValueAsString(requestNewCategoryDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.id", is(notNullValue()), Long.class),
                        jsonPath("$.name", is(requestNewCategoryDto.getName()))
                );

        Category createdCategory = em.createQuery("Select u from Category u", Category.class).getSingleResult();

        assertThat(createdCategory, allOf(
                hasProperty("id", is(notNullValue())),
                hasProperty("name", equalTo(requestNewCategoryDto.getName()))
        ));
    }

    @Test
    void update_withNotExistingId_shouldReturnStatusNotFound() throws Exception {
        long notExistingId = 1000L;
        String message = String.format("Category with id = %s was not found", notExistingId);

        mockMvc.perform(patch("/admin/categories/" + notExistingId)
                        .content(objectMapper.writeValueAsString(requestUpdateCategoryDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.status", is(HttpStatus.NOT_FOUND.name())),
                        jsonPath("$.reason", is("The required object was not found.")),
                        jsonPath("$.message", is(message)),
                        jsonPath("$.timestamp", is(near(LocalDateTime.now(), DATE_PATTERN)))
                );
    }

    @Test
    void update_withNotUniqueName_shouldReturnStatusConflictAndApiError() throws Exception {
        Category existingCategory = createCategory(null, "oldName");
        em.persist(existingCategory);
        Category categoryWithSameName = createCategory(null, name);
        em.persist(categoryWithSameName);
        em.flush();
        Long existingId = existingCategory.getId();

        mockMvc.perform(patch("/admin/categories/" + existingId)
                        .content(objectMapper.writeValueAsString(requestUpdateCategoryDto))
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
    void update_withUniqueName_shouldReturnStatusOkAndDtoOfUpdatedCategory() throws Exception {
        Category existingCategory = createCategory(null, "oldName");
        em.persist(existingCategory);
        em.flush();
        Long existingId = existingCategory.getId();

        mockMvc.perform(patch("/admin/categories/" + existingId)
                        .content(objectMapper.writeValueAsString(requestUpdateCategoryDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", equalTo(existingId), Long.class),
                        jsonPath("$.name", equalTo(requestNewCategoryDto.getName()))
                );
    }

    @Test
    void delete_withNotExistingId_shouldReturnStatusNotFound() throws Exception {
        long notExistingId = 1000L;
        String message = String.format("Category with id = %s was not found", notExistingId);

        mockMvc.perform(delete("/admin/categories/" + notExistingId))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.status", is(HttpStatus.NOT_FOUND.name())),
                        jsonPath("$.reason", is("The required object was not found.")),
                        jsonPath("$.message", is(message)),
                        jsonPath("$.timestamp", is(near(LocalDateTime.now(), DATE_PATTERN)))
                );
    }

    @Test
    void delete_shouldDeleteCategoryAndReturnStatusOk() throws Exception {
        Category existingCategory = createCategory(null, name);
        em.persist(existingCategory);
        em.flush();
        Long existingId = existingCategory.getId();

        mockMvc.perform(delete("/admin/categories/" + existingId))
                .andExpect(status().isNoContent());

        assertThat(em.contains(existingCategory), is(false));
    }
}