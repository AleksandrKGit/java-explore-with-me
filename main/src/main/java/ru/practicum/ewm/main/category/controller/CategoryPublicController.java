package ru.practicum.ewm.main.category.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.category.dto.CategoryDto;
import ru.practicum.ewm.main.category.service.CategoryService;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/categories")
@RequiredArgsConstructor
@Validated
public class CategoryPublicController {
    private final CategoryService service;

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> get(@PathVariable Long categoryId) {
        CategoryDto dto = service.get(categoryId);

        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<CategoryDto>> find(@Min(value = 0, message = "must not be less than 0")
                                                  @RequestParam(required = false, defaultValue = "0") Integer from,
                                                  @Min(value = 1, message = "must not be less than 1")
                                                  @RequestParam(required = false, defaultValue = "10") Integer size) {
        List<CategoryDto> dtoList = service.find(from, size);

        return ResponseEntity.ok(dtoList);
    }
}