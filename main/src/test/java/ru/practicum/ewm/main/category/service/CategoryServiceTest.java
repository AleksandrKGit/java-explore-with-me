package ru.practicum.ewm.main.category.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import ru.practicum.ewm.main.exception.ConflictException;
import ru.practicum.ewm.main.exception.NotFoundException;
import ru.practicum.ewm.main.support.OffsetPageRequest;
import ru.practicum.ewm.main.category.Category;
import ru.practicum.ewm.main.category.CategoryRepository;
import ru.practicum.ewm.main.category.dto.NewCategoryDto;
import ru.practicum.ewm.main.category.dto.CategoryDto;
import ru.practicum.ewm.main.category.dto.CategoryMapperImpl;
import java.util.List;
import java.util.Optional;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static ru.practicum.ewm.main.tools.factories.CategoryFactory.*;
import static ru.practicum.ewm.main.tools.factories.CategoryFactory.copyOf;

@SpringBootTest(classes = {CategoryServiceImpl.class, CategoryMapperImpl.class})
@FieldDefaults(level = AccessLevel.PRIVATE)
class CategoryServiceTest {
    @Autowired
    CategoryService service;

    @MockBean
    CategoryRepository repository;

    final DataIntegrityViolationException nameConstraintException = new DataIntegrityViolationException("name");

    final DataIntegrityViolationException eventsConstraintException = new DataIntegrityViolationException("events");

    final Long id = 1L;

    NewCategoryDto requestNewCategoryDto;

    CategoryDto requestUpdateCategoryDto;

    Category createdCategory;

    Category existingCategory;

    final Integer from = 1;

    final Integer size = 2;

    final OffsetPageRequest pageRequest = OffsetPageRequest.ofOffset(from, size, Sort.by("id").ascending());

    @BeforeEach
    void setUp() {
        requestNewCategoryDto = createNewCategoryDto("n1");
        createdCategory = createCategory(id, requestNewCategoryDto.getName());
        existingCategory = createCategory(id, "n2");
        requestUpdateCategoryDto = createCategoryDto(null, "n3");
    }

    @Test
    void create_withNotUniqueName_shouldThrowDataIntegrityViolationException() {
        ArgumentCaptor<Category> categoryArgumentCaptor = ArgumentCaptor.forClass(Category.class);
        when(repository.saveAndFlush(categoryArgumentCaptor.capture())).thenThrow(nameConstraintException);

        assertThrows(DataIntegrityViolationException.class, () -> service.create(requestNewCategoryDto));
        Category categoryToRepository = categoryArgumentCaptor.getValue();

        assertThat(categoryToRepository, allOf(
                hasProperty("id", is(nullValue())),
                hasProperty("name", equalTo(requestNewCategoryDto.getName()))
        ));
    }

    @Test
    void create_shouldReturnCreatedEntityDto() {
        ArgumentCaptor<Category> categoryArgumentCaptor = ArgumentCaptor.forClass(Category.class);
        when(repository.saveAndFlush(categoryArgumentCaptor.capture())).thenReturn(copyOf(createdCategory));

        CategoryDto resultCategoryDto = service.create(requestNewCategoryDto);
        Category categoryToRepository = categoryArgumentCaptor.getValue();

        assertThat(categoryToRepository, allOf(
                hasProperty("id", is(nullValue())),
                hasProperty("name", equalTo(requestNewCategoryDto.getName()))
        ));

        assertThat(resultCategoryDto, allOf(
                hasProperty("id", equalTo(createdCategory.getId())),
                hasProperty("name", equalTo(createdCategory.getName()))
        ));
    }

    @Test
    void read_shouldReturnDtoListOfCategoriesPage() {
        when(repository.findAll(eq(pageRequest))).thenReturn(new PageImpl<>(List.of(copyOf(existingCategory))));

        List<CategoryDto> resultCategoryDtoList = service.find(from, size);

        assertThat(resultCategoryDtoList, contains(allOf(
                hasProperty("id", equalTo(existingCategory.getId())),
                hasProperty("name", equalTo(existingCategory.getName()))
        )));
    }

    @Test
    void readById_withNotExistingId_shouldThrowNotFoundException() {
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.get(id));
    }

    @Test
    void readById_shouldReturnSelectedCategoryDto() {
        when(repository.findById(id)).thenReturn(Optional.of(copyOf(existingCategory)));

        CategoryDto resultCategoryDto = service.get(id);

        assertThat(resultCategoryDto, allOf(
                hasProperty("id", equalTo(existingCategory.getId())),
                hasProperty("name", equalTo(existingCategory.getName()))
        ));
    }

    @Test
    void update_withNotExistingId_shouldThrowNotFoundException() {
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.update(id, requestUpdateCategoryDto));

        verify(repository, never()).saveAndFlush(any());
    }

    @Test
    void update_withNotUniqueName_shouldThrowDataIntegrityViolationException() {
        ArgumentCaptor<Category> categoryArgumentCaptor = ArgumentCaptor.forClass(Category.class);
        when(repository.findById(id)).thenReturn(Optional.of(existingCategory));
        when(repository.saveAndFlush(categoryArgumentCaptor.capture())).thenThrow(nameConstraintException);

        assertThrows(DataIntegrityViolationException.class, () -> service.update(id, requestUpdateCategoryDto));
        Category categoryToRepository = categoryArgumentCaptor.getValue();

        assertThat(categoryToRepository, allOf(
                hasProperty("id", equalTo(existingCategory.getId())),
                hasProperty("name", equalTo(requestUpdateCategoryDto.getName()))
        ));
    }

    @Test
    void update_withUniqueName_shouldReturnUpdatedCategoryDto() {
        Category updatedCategory = createCategory(existingCategory.getId(), requestUpdateCategoryDto.getName());
        ArgumentCaptor<Category> categoryArgumentCaptor = ArgumentCaptor.forClass(Category.class);
        when(repository.findById(id)).thenReturn(Optional.of(existingCategory));
        when(repository.saveAndFlush(categoryArgumentCaptor.capture())).thenReturn(updatedCategory);

        CategoryDto resultCategoryDto = service.update(id, requestUpdateCategoryDto);
        Category categoryToRepository = categoryArgumentCaptor.getValue();

        assertThat(categoryToRepository, allOf(
                hasProperty("id", equalTo(existingCategory.getId())),
                hasProperty("name", equalTo(requestUpdateCategoryDto.getName()))
        ));
        assertThat(resultCategoryDto, allOf(
                hasProperty("id", equalTo(updatedCategory.getId())),
                hasProperty("name", equalTo(updatedCategory.getName()))
        ));
    }

    @Test
    void delete_withNotExistingId_shouldThrowNotFoundException() {
        doThrow(new EmptyResultDataAccessException(1)).when(repository).deleteById(id);

        assertThrows(NotFoundException.class, () -> service.delete(id));
    }

    @Test
    void delete_withEvents_shouldThrowConflictException() {
        // TODO: check
        doThrow(eventsConstraintException).when(repository).deleteById(id);

        ConflictException target = assertThrows(ConflictException.class, () -> service.delete(id));

        assertThat(target, hasProperty("message", equalTo("The category is not empty")));
    }

    @Test
    void delete_shouldInvokeRepositoryDeleteByIdMethodWithSelectedId() {
        service.delete(id);

        verify(repository, times(1)).deleteById(id);
    }
}