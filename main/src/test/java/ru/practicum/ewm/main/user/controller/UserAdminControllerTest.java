package ru.practicum.ewm.main.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.validation.Errors;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import ru.practicum.ewm.main.exception.NotFoundException;
import ru.practicum.ewm.main.tools.configuration.AppTestConfiguration;
import ru.practicum.ewm.main.user.dto.NewUserRequest;
import ru.practicum.ewm.main.user.dto.UserDto;
import ru.practicum.ewm.main.user.service.UserService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static ru.practicum.ewm.main.tools.factories.UserFactory.createNewUserRequest;
import static ru.practicum.ewm.main.tools.factories.UserFactory.createUserDto;
import static ru.practicum.ewm.main.tools.matchers.LocalDateTimeMatcher.near;
import static ru.practicum.ewm.main.tools.matchers.NewUserRequestMatcher.equalTo;

@WebMvcTest(UserAdminController.class)
@SpringJUnitConfig({AppTestConfiguration.class})
@FieldDefaults(level = AccessLevel.PRIVATE)
class UserAdminControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserService service;

    @SpyBean
    LocalValidatorFactoryBean validator;

    static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    static final long validId = 10L;

    final NewUserRequest validDto = createNewUserRequest("userName", "user@email.com");

    final UserDto resultDto = createUserDto(1L, "user1", "email1");

    final List<UserDto> resultListDto = List.of(resultDto);

    private static Stream<Arguments> httpMediaTypeNotSupportedRequests() {
        String validUserJson = "{\"name\":\"userName\",\"email\":\"user@email.com\"}";

        return Stream.of(
                Arguments.of("create without media type",
                        post("/admin/users")
                                .content(validUserJson)),

                Arguments.of("create with incorrect media type",
                        post("/admin/users")
                                .content(validUserJson)
                                .contentType(MediaType.IMAGE_PNG))
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("httpMediaTypeNotSupportedRequests")
    void request_withNotSupportedMediaType_shouldReturnStatusUnsupportedMediaTypeAndApiError(
            String testName, MockHttpServletRequestBuilder request) throws Exception {
        mockMvc.perform(request)
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.status", is(HttpStatus.UNSUPPORTED_MEDIA_TYPE.name())));
    }

    private static Stream<Arguments> badRequest() {
        String incorrectId = "a";
        String incorrectFrom = "a";
        String incorrectSize = "a";
        String incorrectJson = "}";

        return Stream.of(
                Arguments.of("read with incorrect ids",
                        get("/admin/users?ids=" + incorrectId)),

                Arguments.of("read with incorrect from",
                        get("/admin/users?from=" + incorrectFrom)),

                Arguments.of("read with incorrect size",
                        get("/admin/users?size=" + incorrectSize)),

                Arguments.of("create without request body",
                        post("/admin/users")
                                .contentType(MediaType.APPLICATION_JSON)),

                Arguments.of("create with incorrect json",
                        post("/admin/users")
                                .content(incorrectJson)
                                .contentType(MediaType.APPLICATION_JSON)),

                Arguments.of("delete with incorrect id",
                        delete("/admin/users/" + incorrectId))
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("badRequest")
    void request_withIncorrectRequest_shouldReturnStatusBadRequestAndApiError(
            String testName, MockHttpServletRequestBuilder request) throws Exception {
        mockMvc.perform(request)
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.status", is(HttpStatus.BAD_REQUEST.name())),
                        jsonPath("$.reason", is("Incorrectly made request.")),
                        jsonPath("$.timestamp", is(near(LocalDateTime.now(), DATE_PATTERN)))
                );
    }

    private static Stream<Arguments> invalidRequest() {
        String invalidFrom = "-1";
        String invalidSize = "0";
        String invalidJson = "{\"name\":\"name\",\"email\":\"\"}";

        return Stream.of(
                Arguments.of("read with invalid from",
                        get("/admin/users?from=" + invalidFrom),
                        "Field: from. Error: must not be less than 0. Value: -1"),

                Arguments.of("read with invalid size",
                        get("/admin/users?size=" + invalidSize),
                        "Field: size. Error: must not be less than 1. Value: 0"),

                Arguments.of("create with invalid json",
                        post("/admin/users")
                                .content(invalidJson)
                                .contentType(MediaType.APPLICATION_JSON),
                        "Field: email. Error: must have correct email format. Value: ")
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidRequest")
    void request_withInvalidRequest_shouldReturnStatusBadRequestAndApiError(
            String testName, MockHttpServletRequestBuilder request, String message) throws Exception {
        mockMvc.perform(request)
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.status", is(HttpStatus.BAD_REQUEST.name())),
                        jsonPath("$.reason", is("Incorrectly made request.")),
                        jsonPath("$.message", is(message)),
                        jsonPath("$.timestamp", is(near(LocalDateTime.now(), DATE_PATTERN)))
                );
    }

    @Test
    void request_withCreateEndPointAndDataIntegrityViolationExceptionThrownByServiceCreateMethod_shouldReturnStatusConflictAndApiError()
            throws Exception {
        String message = "email constraint";
        DataIntegrityViolationException exception = new DataIntegrityViolationException(message);

        when(service.create(argThat(equalTo(validDto)))).thenThrow(exception);

        mockMvc.perform(post("/admin/users")
                        .content(objectMapper.writeValueAsString(validDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isConflict(),
                        jsonPath("$.status", is(HttpStatus.CONFLICT.name())),
                        jsonPath("$.reason", is("Integrity constraint has been violated.")),
                        jsonPath("$.message", is(message)),
                        jsonPath("$.timestamp", is(near(LocalDateTime.now(), DATE_PATTERN)))
                );
    }

    @Test
    void request_withCreateEndPointAndRuntimeExceptionThrownByServiceCreateMethod_shouldReturnStatusInternalServerErrorAndApiError()
            throws Exception {
        String message = "exceptionMessage";
        RuntimeException exception = new RuntimeException(message);
        when(service.create(argThat(equalTo(validDto)))).thenThrow(exception);

        mockMvc.perform(post("/admin/users")
                        .content(objectMapper.writeValueAsString(validDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isInternalServerError(),
                        jsonPath("$.status", is(HttpStatus.INTERNAL_SERVER_ERROR.name())),
                        jsonPath("$.reason", is("Main service error.")),
                        jsonPath("$.message", is(message)),
                        jsonPath("$.timestamp", is(near(LocalDateTime.now(), DATE_PATTERN)))
                );
    }

    @Test
    void request_withCreateEndPoint_shouldReturnStatusCreatedAndServiceCreateMethodResult()
            throws Exception {
        when(service.create(argThat(equalTo(validDto)))).thenReturn(resultDto);

        mockMvc.perform(post("/admin/users")
                        .content(objectMapper.writeValueAsString(validDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(resultDto)))
                .andExpect(status().isCreated());

        verify(validator, times(1))
                .validate(argThat(equalTo(validDto)), (Errors) any());
    }

    @Test
    void request_withReadEndPointWithParams_shouldReturnStatusOkAndServiceFindMethodResultInvokedWithParams()
            throws Exception {
        List<Long> ids = List.of(1L, 1000L);
        String idsParam = ids.stream().map(String::valueOf).collect(Collectors.joining(","));
        Integer from = 1;
        Integer size = 2;

        when(service.find(ids, from, size)).thenReturn(resultListDto);

        mockMvc.perform(get(String.format("/admin/users?ids=%s&from=%s&size=%s", idsParam, from, size)))
                .andExpect(content().json(objectMapper.writeValueAsString(resultListDto)))
                .andExpect(status().isOk());
    }

    @Test
    void request_withReadEndPointWithoutParams_shouldReturnStatusOkAndServiceFindMethodResultInvokedWithNullIdsAndDefaultParams()
            throws Exception {
        when(service.find(null, 0, 10)).thenReturn(resultListDto);

        mockMvc.perform(get("/admin/users"))
                .andExpect(content().json(objectMapper.writeValueAsString(resultListDto)))
                .andExpect(status().isOk());
    }

    @Test
    void request_withDeleteEndPointAndNotFoundExceptionThrownByServiceDeleteMethod_shouldReturnStatusNotFoundAndApiError()
            throws Exception {
        String message = String.format("User with id = %s was not found", validId);
        NotFoundException exception = new NotFoundException(message);

        doThrow(exception).when(service).delete(validId);

        mockMvc.perform(delete("/admin/users/" + validId))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.status", is(HttpStatus.NOT_FOUND.name())),
                        jsonPath("$.reason", is("The required object was not found.")),
                        jsonPath("$.message", is(message)),
                        jsonPath("$.timestamp", is(near(LocalDateTime.now(), DATE_PATTERN)))
                );
    }

    @Test
    void request_withDeleteEndPoint_shouldReturnStatusNoContentAndInvokeServiceDeleteMethod() throws Exception {
        mockMvc.perform(delete("/admin/users/" + validId))
                .andExpect(status().isNoContent());

        verify(service, times(1)).delete(validId);
    }
}