package ru.practicum.ewm.main.category.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.ewm.main.controller.ControllerErrorHandler;
import ru.practicum.ewm.main.category.Category;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.ewm.main.tools.factories.CategoryFactory.createCategory;
import static ru.practicum.ewm.main.tools.matchers.LocalDateTimeMatcher.near;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryPublicControllerIntegrationTests {
    MockMvc mockMvc;

    @Autowired
    CategoryPublicController controller;

    @Autowired
    EntityManager em;

    @Autowired
    ControllerErrorHandler controllerErrorHandler;

    static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(controllerErrorHandler)
                .build();
    }

    @Test
    void read_shouldReturnDtoListOfSelectedCategories() throws Exception {
        Category category1 = createCategory(null, "n1");
        Category category2 = createCategory(null, "n2");
        Category category3 = createCategory(null, "n3");
        Category category4 = createCategory(null, "n4");
        em.persist(category1);
        em.persist(category2);
        em.persist(category3);
        em.persist(category4);
        em.flush();
        int from = 1;
        int size = 2;

        mockMvc.perform(get("/categories?from=" + from + "&size=" + size))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$[0].id", is(category2.getId()), Long.class),
                        jsonPath("$[0].name", is(category2.getName())),
                        jsonPath("$[1].id", is(category3.getId()), Long.class),
                        jsonPath("$[1].name", is(category3.getName()))
                );
    }

    @Test
    void read_withNoCategoriesFound_shouldReturnEmptyDtoList() throws Exception {
        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    void readById_withNotExistingId_shouldReturnStatusNotFound() throws Exception {
        long notExistingId = 1000L;
        String message = "Category with id=" + notExistingId + " was not found";

        mockMvc.perform(get("/categories/" + notExistingId))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.status", is(HttpStatus.NOT_FOUND.name())),
                        jsonPath("$.reason", is("The required object was not found.")),
                        jsonPath("$.message", equalTo(message)),
                        jsonPath("$.timestamp", is(near(LocalDateTime.now(), DATE_PATTERN)))
                );
    }

    @Test
    void readById_shouldReturnStatusOKAndSelectedCategoryDto() throws Exception {
        Category category = createCategory(null, "categoryName");
        em.persist(category);
        em.flush();

        mockMvc.perform(get("/categories/" + category.getId()))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", equalTo(category.getId()), Long.class),
                        jsonPath("$.name", equalTo(category.getName()))
                );
    }
}
