package ru.practicum.ewm.main.category.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.category.dto.NewCategoryDto;
import ru.practicum.ewm.main.category.dto.CategoryDto;
import ru.practicum.ewm.main.category.service.CategoryService;
import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
@Validated
public class CategoryAdminController {
    private final CategoryService service;

    @PostMapping
    public ResponseEntity<CategoryDto> create(@Valid @RequestBody NewCategoryDto inDto) {
        CategoryDto outDto = service.create(inDto);

        return new ResponseEntity<>(outDto, HttpStatus.CREATED);
    }

    @PatchMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> update(@PathVariable Long categoryId, @Valid @RequestBody CategoryDto inDto) {
        CategoryDto dto = service.update(categoryId, inDto);

        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> delete(@PathVariable Long categoryId) {
        service.delete(categoryId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}