package ru.practicum.ewm.main.user.service;

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
import ru.practicum.ewm.main.exception.NotFoundException;
import ru.practicum.ewm.main.support.OffsetPageRequest;
import ru.practicum.ewm.main.user.User;
import ru.practicum.ewm.main.user.UserRepository;
import ru.practicum.ewm.main.user.dto.NewUserRequest;
import ru.practicum.ewm.main.user.dto.UserDto;
import ru.practicum.ewm.main.user.dto.UserMapperImpl;
import java.util.List;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static ru.practicum.ewm.main.tools.factories.UserFactory.*;

@SpringBootTest(classes = {UserServiceImpl.class, UserMapperImpl.class})
@FieldDefaults(level = AccessLevel.PRIVATE)
class UserServiceTest {
    @Autowired
    UserService service;

    @MockBean
    UserRepository repository;

    final DataIntegrityViolationException emailConstraintException = new DataIntegrityViolationException("email");

    final Long id = 1L;

    NewUserRequest requestUserDto;

    User createdUser;

    User existingUser;

    final Integer from = 1;

    final Integer size = 2;

    final OffsetPageRequest pageRequest = OffsetPageRequest.ofOffset(from, size, Sort.by("id").ascending());

    @BeforeEach
    void setUp() {
        requestUserDto = createNewUserRequest("n1", "e1");
        createdUser = createUser(id, requestUserDto.getName(), requestUserDto.getEmail());
        existingUser = createUser(id, "n2", "e2");
    }

    @Test
    void create_withNotUniqueEmail_shouldThrowDataIntegrityViolationException() {
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        when(repository.saveAndFlush(userArgumentCaptor.capture())).thenThrow(emailConstraintException);

        assertThrows(DataIntegrityViolationException.class, () -> service.create(requestUserDto));
        User userToRepository = userArgumentCaptor.getValue();

        assertThat(userToRepository, allOf(
                hasProperty("id", is(nullValue())),
                hasProperty("name", equalTo(requestUserDto.getName())),
                hasProperty("email", equalTo(requestUserDto.getEmail()))
        ));
    }

    @Test
    void create_shouldReturnCreatedEntityDto() {
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        when(repository.saveAndFlush(userArgumentCaptor.capture())).thenReturn(copyOf(createdUser));

        UserDto resultUserDto = service.create(requestUserDto);
        User userToRepository = userArgumentCaptor.getValue();

        assertThat(userToRepository, allOf(
                hasProperty("id", is(nullValue())),
                hasProperty("name", equalTo(requestUserDto.getName())),
                hasProperty("email", equalTo(requestUserDto.getEmail()))
        ));

        assertThat(resultUserDto, allOf(
                hasProperty("id", equalTo(createdUser.getId())),
                hasProperty("name", equalTo(createdUser.getName())),
                hasProperty("email", equalTo(createdUser.getEmail()))
        ));
    }

    @Test
    void find_withNullIds_shouldReturnDtoListOfAllUsersPage() {
        when(repository.findAll(eq(pageRequest))).thenReturn(new PageImpl<>(List.of(copyOf(existingUser))));

        List<UserDto> resultUserDtoList = service.find(null, from, size);

        assertThat(resultUserDtoList, contains(allOf(
                hasProperty("id", equalTo(existingUser.getId())),
                hasProperty("name", equalTo(existingUser.getName())),
                hasProperty("email", equalTo(existingUser.getEmail()))
        )));
    }

    @Test
    void find_withNotNullIds_shouldReturnDtoListOfUsersPageWithSelectedIds() {
        List<Long> ids = List.of(id);
        when(repository.findByIdIn(eq(ids), eq(pageRequest))).thenReturn(new PageImpl<>(List.of(copyOf(existingUser))));

        List<UserDto> resultUserDtoList = service.find(ids, from, size);

        assertThat(resultUserDtoList, contains(allOf(
                hasProperty("id", equalTo(existingUser.getId())),
                hasProperty("name", equalTo(existingUser.getName())),
                hasProperty("email", equalTo(existingUser.getEmail()))
        )));
    }

    @Test
    void delete_withNotExistingId_shouldThrowNotFoundException() {
        doThrow(new EmptyResultDataAccessException(1)).when(repository).deleteById(id);

        assertThrows(NotFoundException.class, () -> service.delete(id));
    }

    @Test
    void delete_shouldInvokeRepositoryDeleteByIdMethodWithSelectedId() {
        service.delete(id);

        verify(repository, times(1)).deleteById(id);
    }
}