package ru.practicum.ewm.main.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.user.dto.NewUserRequest;
import ru.practicum.ewm.main.user.dto.UserDto;
import ru.practicum.ewm.main.user.service.UserService;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
@Validated
public class UserAdminController {
    private final UserService service;

    @PostMapping
    public ResponseEntity<UserDto> create(@Valid @RequestBody NewUserRequest inDto) {
        UserDto outDto = service.create(inDto);

        return new ResponseEntity<>(outDto, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> find(@RequestParam(value = "ids", required = false) List<Long> ids,
                                              @Min(value = 0, message = "must not be less than 0")
                                              @RequestParam(required = false, defaultValue = "0") Integer from,
                                              @Min(value = 1, message = "must not be less than 1")
                                              @RequestParam(required = false, defaultValue = "10") Integer size) {
        List<UserDto> dtoList = service.find(ids, from, size);

        return ResponseEntity.ok(dtoList);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable Long userId) {
        service.delete(userId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
