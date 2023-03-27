package ru.practicum.ewm.main.compilation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.compilation.dto.CompilationDto;
import ru.practicum.ewm.main.compilation.service.CompilationService;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/compilations")
@RequiredArgsConstructor
@Validated
public class CompilationPublicController {
    private final CompilationService service;

    @GetMapping("/{compId}")
    public ResponseEntity<CompilationDto> get(@PathVariable Long compId) {
        CompilationDto dto = service.get(compId);

        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<CompilationDto>> find(@RequestParam(required = false)
                                                     Boolean pinned,
                                                     @RequestParam(required = false, defaultValue = "0")
                                                     @Min(value = 0, message = "must not be less than 0")
                                                     Integer from,
                                                     @RequestParam(required = false, defaultValue = "10")
                                                     @Min(value = 1, message = "must not be less than 1")
                                                     Integer size) {
        List<CompilationDto> dtoList = service.find(pinned, from, size);

        return ResponseEntity.ok(dtoList);
    }
}