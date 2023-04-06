package ru.practicum.ewm.main.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.comment.dto.CommentDto;
import ru.practicum.ewm.main.comment.dto.CommentFullDto;
import ru.practicum.ewm.main.comment.dto.CommentShortDto;
import ru.practicum.ewm.main.comment.service.CommentService;
import ru.practicum.ewm.main.validation.constraints.CommentState;
import ru.practicum.ewm.main.validation.constraints.DateOfPattern;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;
import static ru.practicum.ewm.common.support.DateFactory.*;

@RestController
@RequestMapping(path = "/users/{userId}/comments")
@RequiredArgsConstructor
@Validated
public class CommentPrivateController {
    private final CommentService service;

    @PostMapping
    public ResponseEntity<CommentShortDto> create(@PathVariable Long userId,
                                                  @RequestParam Long eventId,
                                                  @Valid @RequestBody CommentDto inDto) {
        CommentShortDto outDto = service.create(userId, eventId, inDto);

        return new ResponseEntity<>(outDto, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CommentShortDto>> findByCommentator(@PathVariable Long userId,
                                                                   @CommentState
                                                                   @RequestParam(required = false) String state,
                                                                   @DateOfPattern(pattern = DATE_FORMAT)
                                                                   @RequestParam(required = false) String rangeStart,
                                                                   @DateOfPattern(pattern = DATE_FORMAT)
                                                                   @RequestParam(required = false) String rangeEnd,
                                                                   @Min(value = 0, message = "must not be less than 0")
                                                                   @RequestParam(required = false, defaultValue = "0")
                                                                   Integer from,
                                                                   @Min(value = 1, message = "must not be less than 1")
                                                                   @RequestParam(required = false, defaultValue = "10")
                                                                   Integer size) {
        List<CommentShortDto> dtoList = service.findByCommentator(userId, state, dateOf(rangeStart), dateOf(rangeEnd),
                from, size);

        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/events")
    public ResponseEntity<List<CommentFullDto>> findByInitiator(@PathVariable Long userId,
                                                                @RequestParam(required = false) Long eventId,
                                                                @CommentState
                                                                @RequestParam(required = false) String state,
                                                                @DateOfPattern(pattern = DATE_FORMAT)
                                                                @RequestParam(required = false) String rangeStart,
                                                                @DateOfPattern(pattern = DATE_FORMAT)
                                                                @RequestParam(required = false) String rangeEnd,
                                                                @Min(value = 0, message = "must not be less than 0")
                                                                @RequestParam(required = false, defaultValue = "0")
                                                                Integer from,
                                                                @Min(value = 1, message = "must not be less than 1")
                                                                @RequestParam(required = false, defaultValue = "10")
                                                                Integer size) {
        List<CommentFullDto> dtoList = service.findByInitiator(userId, eventId, state, dateOf(rangeStart),
                dateOf(rangeEnd), from, size);

        return ResponseEntity.ok(dtoList);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CommentShortDto> update(@PathVariable Long userId,
                                                  @PathVariable Long id,
                                                  @Valid @RequestBody CommentDto inDto) {
        CommentShortDto outDto = service.update(userId, id, inDto);

        return ResponseEntity.ok(outDto);
    }

    @PatchMapping("/publish")
    public ResponseEntity<List<CommentFullDto>> publish(@PathVariable Long userId,
                                                        @RequestParam List<Long> ids) {
        List<CommentFullDto> dtoList = service.publishByInitiator(userId, ids);

        return ResponseEntity.ok(dtoList);
    }

    @PatchMapping("/reject")
    public ResponseEntity<List<CommentFullDto>> reject(@PathVariable Long userId,
                                                        @RequestParam List<Long> ids) {
        List<CommentFullDto> dtoList = service.rejectByInitiator(userId, ids);

        return ResponseEntity.ok(dtoList);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long userId,
                                       @PathVariable Long id) {
        service.deleteByCommentator(userId, id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}