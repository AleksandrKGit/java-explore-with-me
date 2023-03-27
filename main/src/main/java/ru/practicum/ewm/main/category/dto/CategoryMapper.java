package ru.practicum.ewm.main.category.dto;

import org.mapstruct.*;
import ru.practicum.ewm.main.category.Category;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(target = "id", ignore = true)
    Category toEntity(NewCategoryDto dto);

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(CategoryDto dto, @MappingTarget Category category);

    CategoryDto toDto(Category category);

    List<CategoryDto> toDto(List<Category> categories);
}