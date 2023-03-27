package ru.practicum.ewm.main.user.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.main.exception.NotFoundException;
import ru.practicum.ewm.main.support.OffsetPageRequest;
import ru.practicum.ewm.main.user.User;
import ru.practicum.ewm.main.user.UserRepository;
import ru.practicum.ewm.main.user.dto.NewUserRequest;
import ru.practicum.ewm.main.user.dto.UserDto;
import ru.practicum.ewm.main.user.dto.UserMapper;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    UserRepository repository;

    UserMapper mapper;

    @Override
    public UserDto create(NewUserRequest dto) {
        User user = mapper.toEntity(dto);

        return mapper.toDto(repository.saveAndFlush(user));
    }

    @Override
    public List<UserDto> find(@Nullable List<Long> ids, Integer from, Integer size) {
        Pageable pageRequest = OffsetPageRequest.ofOffset(from, size, Sort.by("id").ascending());

        Page<User> users;

        if (ids == null) {
            users = repository.findAll(pageRequest);
        } else {
            users = repository.findByIdIn(ids, pageRequest);
        }

        return mapper.toDto(users.toList());
    }

    @Override
    public void delete(Long id) {
        try {
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException ignored) {
            throw new NotFoundException("User with id = " + id + " was not found");
        }
    }
}
