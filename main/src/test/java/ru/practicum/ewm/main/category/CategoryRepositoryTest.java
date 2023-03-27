package ru.practicum.ewm.main.category;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.logging.log4j.util.Strings;
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
import ru.practicum.ewm.main.support.OffsetPageRequest;

import javax.transaction.Transactional;
import java.util.stream.Stream;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.ewm.main.tools.factories.CategoryFactory.createCategory;

@Transactional
@DataJpaTest
@FieldDefaults(level = AccessLevel.PRIVATE)
class CategoryRepositoryTest {
    @Autowired
    TestEntityManager em;

    @Autowired
    CategoryRepository repository;

    static final Long notExistingId = 1000L;

    static final String name = "categoryName";

    final Sort sort = Sort.by("id").ascending();

    private static Stream<String> save() {
        return Stream.of("", name);
    }

    @ParameterizedTest(name = "name={0}")
    @MethodSource("save")
    void save_withWithNullId_shouldReturnAttachedTransferredCategoryWithGeneratedId(String name) {
        Category category = createCategory(null, name);

        Category savedCategory = repository.saveAndFlush(category);

        assertThat(savedCategory == category, is(true));
        assertThat(savedCategory, hasProperty("id", is(not(nullValue()))));
    }

    @ParameterizedTest(name = "name={0}, email={1}")
    @MethodSource("save")
    void save_withWithNotExistingId_shouldReturnNewAttachedCategoryWithGeneratedId(String name) {
        Category category = createCategory(notExistingId, name);

        Category savedCategory = repository.saveAndFlush(category);

        assertThat(category, hasProperty("id", is(notNullValue())));
        assertThat(savedCategory == category, is(false));
        assertThat(savedCategory, allOf(
                hasProperty("id", not(nullValue())),
                hasProperty("id", not(equalTo(category.getId()))),
                hasProperty("name", equalTo(category.getName()))
        ));
    }


    private static Stream<Arguments> incorrectFields() {
        return Stream.of(
                Arguments.of("null name", null),
                Arguments.of("big name", Strings.repeat("n", 256))
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("incorrectFields")
    void save_withIncorrectFields_shouldThrowException(String testName, String name) {
        Category category = createCategory(null, name);

        assertThrows(Exception.class, () -> repository.saveAndFlush(category));
    }

    @Test
    void save_withNotUniqueName_shouldThrowDataIntegrityViolationException() {
        Category existingCategory = createCategory(null, name);
        em.persist(existingCategory);
        Category category = createCategory(null, name);

        assertThrows(DataIntegrityViolationException.class, () -> repository.saveAndFlush(category));
    }

    @Test
    void delete_withNotExistingId_shouldThrowEmptyResultDataAccessException() {
        assertThrows(EmptyResultDataAccessException.class, () -> repository.deleteById(notExistingId));
    }

    @Test
    void delete_shouldRemoveCategoryWithSuchId() {
        Category existingCategory = createCategory(null, name);
        em.persist(existingCategory);
        em.flush();
        Long existingId = existingCategory.getId();

        repository.deleteById(existingId);

        assertThat(repository.findById(existingId).isEmpty(), is(true));
    }

    @Test
    void delete_withEvents_shouldThrowDataIntegrityViolationException() {
        Category existingCategory = createCategory(null, name);
        em.persist(existingCategory);
        /* TODO
        Long existingId = existingCategory.getId();
        Event event = createEvent(..., existingId, ...);
        em.persist(event);
        */
        em.flush();

        // assertThrows(DataIntegrityViolationException.class, () -> repository.deleteById(existingId));
    }

    private static Stream<String> update() {
        return Stream.of("", name, "newName");
    }

    @ParameterizedTest(name = "name=oldName => {0}")
    @MethodSource("update")
    void update_shouldUpdateCategoryFields(String name) {
        Category existingCategory = createCategory(null, "oldName");
        em.persist(existingCategory);
        em.flush();
        Long existingId = existingCategory.getId();
        Category category = createCategory(existingId, name);

        Category updatedCategory = repository.saveAndFlush(category);

        assertThat(updatedCategory == existingCategory, is(true));
        assertThat(updatedCategory == category, is(false));
        assertThat(updatedCategory, allOf(
                hasProperty("id", equalTo(existingId)),
                hasProperty("name", equalTo(category.getName()))
        ));
    }

    @Test
    void findAll_withNoCategories_shouldReturnEmptyPage() {
        Pageable pageRequest = OffsetPageRequest.ofOffset(0, 10, sort);

        Page<Category> result = repository.findAll(pageRequest);

        assertThat(result, is(emptyIterable()));
    }

    @Test
    void findAll_withOffsetPageRequest_shouldReturnPageWithCategories() {
        Integer from = 1;
        Integer size = 2;
        Pageable pageRequest = OffsetPageRequest.ofOffset(from, size, sort);

        Category category1 = createCategory(null, "n1");
        Category category2 = createCategory(null, "n2");
        Category category3 = createCategory(null, "n3");
        Category category4 = createCategory(null, "n4");
        em.persist(category1);
        em.persist(category2);
        em.persist(category3);
        em.persist(category4);

        Page<Category> result = repository.findAll(pageRequest);

        assertThat(result, contains(
                category2,
                category3
        ));
    }
}