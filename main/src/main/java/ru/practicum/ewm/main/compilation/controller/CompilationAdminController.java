package ru.practicum.ewm.main.compilation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.compilation.dto.CompilationDto;
import ru.practicum.ewm.main.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.main.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.main.compilation.service.CompilationService;
import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/compilations")
@RequiredArgsConstructor
@Validated
public class CompilationAdminController {
    private final CompilationService service;

    @PostMapping
    public ResponseEntity<CompilationDto> create(@Valid @RequestBody NewCompilationDto inDto) {
        CompilationDto outDto = service.create(inDto);

        return new ResponseEntity<>(outDto, HttpStatus.CREATED);
    }

    @PatchMapping("/{compId}")
    public ResponseEntity<CompilationDto> update(@PathVariable Long compId,
                                                 @Valid @RequestBody UpdateCompilationRequest inDto) {
        CompilationDto dto = service.update(compId, inDto);

        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{compId}")
    public ResponseEntity<Void> delete(@PathVariable Long compId) {
        service.delete(compId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}