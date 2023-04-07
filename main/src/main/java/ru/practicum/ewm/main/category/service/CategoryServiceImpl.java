package ru.practicum.ewm.main.category.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.main.category.Category;
import ru.practicum.ewm.main.category.CategoryRepository;
import ru.practicum.ewm.main.category.dto.CategoryDto;
import ru.practicum.ewm.main.category.dto.CategoryMapper;
import ru.practicum.ewm.main.category.dto.NewCategoryDto;
import ru.practicum.ewm.main.exception.ConflictException;
import ru.practicum.ewm.main.exception.NotFoundException;
import ru.practicum.ewm.main.support.OffsetPageRequest;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryServiceImpl implements CategoryService {
    CategoryRepository repository;

    CategoryMapper mapper;

    @Override
    public CategoryDto create(NewCategoryDto dto) {
        return mapper.toDto(repository.saveAndFlush(mapper.toEntity(dto)));
    }

    @Override
    public CategoryDto get(Long id) {
        Category entity = repository.findById(id).orElse(null);

        if (entity == null) {
            throw new NotFoundException(String.format("Category with id = %s was not found", id));
        }

        return mapper.toDto(entity);
    }

    @Override
    public List<CategoryDto> find(Integer from, Integer size) {
        Pageable pageRequest = OffsetPageRequest.ofOffset(from, size, Sort.by("id").ascending());

        return mapper.toDto(repository.findAll(pageRequest).toList());
    }

    @Override
    public CategoryDto update(Long id, CategoryDto dto) {
        Category entity = repository.findById(id).orElse(null);

        if (entity == null) {
            throw new NotFoundException(String.format("Category with id = %s was not found", id));
        }

        mapper.updateEntityFromDto(dto, entity);

        return mapper.toDto(repository.saveAndFlush(entity));
    }

    @Override
    public void delete(Long id) {
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException ignored) {
            throw new ConflictException("The category is not empty");
        } catch (EmptyResultDataAccessException ignored) {
            throw new NotFoundException(String.format("Category with id = %s was not found", id));
        }
    }
}
