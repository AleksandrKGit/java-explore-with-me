package ru.practicum.ewm.main.category.controller;

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
import ru.practicum.ewm.main.exception.ConflictException;
import ru.practicum.ewm.main.exception.NotFoundException;
import ru.practicum.ewm.main.tools.configuration.AppTestConfiguration;
import ru.practicum.ewm.main.category.dto.NewCategoryDto;
import ru.practicum.ewm.main.category.dto.CategoryDto;
import ru.practicum.ewm.main.category.service.CategoryService;
import java.time.LocalDateTime;
import java.util.stream.Stream;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.ewm.main.tools.factories.CategoryFactory.createNewCategoryDto;
import static ru.practicum.ewm.main.tools.factories.CategoryFactory.createCategoryDto;
import static ru.practicum.ewm.main.tools.matchers.LocalDateTimeMatcher.near;
import static ru.practicum.ewm.main.tools.matchers.NewCategoryDtoMatcher.equalTo;
import static ru.practicum.ewm.main.tools.matchers.CategoryDtoMatcher.equalTo;

@WebMvcTest(CategoryAdminController.class)
@SpringJUnitConfig({AppTestConfiguration.class})
@FieldDefaults(level = AccessLevel.PRIVATE)
class CategoryAdminControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    CategoryService service;

    @SpyBean
    LocalValidatorFactoryBean validator;

    static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    static final long validId = 10L;

    final NewCategoryDto validNewDto = createNewCategoryDto("categoryName");

    final CategoryDto validUpdateDto = createCategoryDto(null, "newCategoryName");

    final CategoryDto resultDto = createCategoryDto(1L, "category1");

    private static Stream<Arguments> httpMediaTypeNotSupportedRequests() {
        String validCategoryJson = "{\"name\":\"categoryName\"}";

        return Stream.of(
                Arguments.of("create without media type",
                        post("/admin/categories")
                                .content(validCategoryJson)),

                Arguments.of("create with incorrect media type",
                        post("/admin/categories")
                                .content(validCategoryJson)
                                .contentType(MediaType.IMAGE_PNG)),

                Arguments.of("update without media type",
                        patch("/admin/categories/" + validId)
                                .content(validCategoryJson)),

                Arguments.of("update with incorrect media type",
                        patch("/admin/categories/" + validId)
                                .content(validCategoryJson)
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
        String incorrectJson = "}";
        String validCategoryJson = "{\"name\":\"categoryName\"}";

        return Stream.of(
                Arguments.of("create without request body",
                        post("/admin/categories")
                                .contentType(MediaType.APPLICATION_JSON)),

                Arguments.of("create with incorrect json",
                        post("/admin/categories")
                                .content(incorrectJson)
                                .contentType(MediaType.APPLICATION_JSON)),

                Arguments.of("update with incorrect id",
                        patch("/admin/categories/" + incorrectId)
                                .content(validCategoryJson)
                                .contentType(MediaType.APPLICATION_JSON)),

                Arguments.of("update without request body",
                        patch("/admin/categories/" + validId)
                                .contentType(MediaType.APPLICATION_JSON)),

                Arguments.of("update with incorrect json",
                        patch("/admin/categories/" + validId)
                                .content(incorrectJson)
                                .contentType(MediaType.APPLICATION_JSON)),

                Arguments.of("delete with incorrect id",
                        delete("/admin/categories/" + incorrectId))
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
        String invalidJson = "{\"name\":\"\"}";

        return Stream.of(
                Arguments.of("create with invalid json",
                        post("/admin/categories")
                                .content(invalidJson)
                                .contentType(MediaType.APPLICATION_JSON),
                        "Field: name. Error: must not be blank. Value: "),

                Arguments.of("update with invalid json",
                        patch("/admin/categories/" + validId)
                                .content(invalidJson)
                                .contentType(MediaType.APPLICATION_JSON),
                        "Field: name. Error: must not be blank. Value: ")
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
        String message = "name constraint";
        DataIntegrityViolationException exception = new DataIntegrityViolationException(message);

        when(service.create(argThat(equalTo(validNewDto)))).thenThrow(exception);

        mockMvc.perform(post("/admin/categories")
                        .content(objectMapper.writeValueAsString(validNewDto))
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
        when(service.create(argThat(equalTo(validNewDto)))).thenThrow(exception);

        mockMvc.perform(post("/admin/categories")
                        .content(objectMapper.writeValueAsString(validNewDto))
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
        when(service.create(argThat(equalTo(validNewDto)))).thenReturn(resultDto);

        mockMvc.perform(post("/admin/categories")
                        .content(objectMapper.writeValueAsString(validNewDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(resultDto)))
                .andExpect(status().isCreated());

        verify(validator, times(1))
                .validate(argThat(equalTo(validNewDto)), (Errors) any());
    }

    @Test
    void request_withUpdateEndPointAndDataIntegrityViolationExceptionThrownByServiceCreateMethod_shouldReturnStatusConflictAndApiError()
            throws Exception {
        String message = "name constraint";
        DataIntegrityViolationException exception = new DataIntegrityViolationException(message);

        when(service.update(eq(validId), argThat(equalTo(validUpdateDto)))).thenThrow(exception);

        mockMvc.perform(patch("/admin/categories/" + validId)
                        .content(objectMapper.writeValueAsString(validUpdateDto))
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
    void request_withUpdateEndPointAndNotFoundExceptionThrownByServiceCreateMethod_shouldReturnStatusNotFoundAndApiError()
            throws Exception {
        String message = String.format("Category with id = %s was not found", validId);
        NotFoundException exception = new NotFoundException(message);

        when(service.update(eq(validId), argThat(equalTo(validUpdateDto)))).thenThrow(exception);

        mockMvc.perform(patch("/admin/categories/" + validId)
                        .content(objectMapper.writeValueAsString(validUpdateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.status", is(HttpStatus.NOT_FOUND.name())),
                        jsonPath("$.reason", is("The required object was not found.")),
                        jsonPath("$.message", is(message)),
                        jsonPath("$.timestamp", is(near(LocalDateTime.now(), DATE_PATTERN)))
                );
    }

    @Test
    void request_withUpdateEndPoint_shouldReturnStatusOkAndServiceUpdateMethodResult()
            throws Exception {
        when(service.update(eq(validId), argThat(equalTo(validUpdateDto)))).thenReturn(resultDto);

        mockMvc.perform(patch("/admin/categories/" + validId)
                        .content(objectMapper.writeValueAsString(validUpdateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(resultDto)))
                .andExpect(status().isOk());

        verify(validator, times(1))
                .validate(argThat(equalTo(validUpdateDto)), (Errors) any());
    }

    @Test
    void request_withDeleteEndPointAndNotFoundExceptionThrownByServiceDeleteMethod_shouldReturnStatusNotFoundAndApiError()
            throws Exception {
        String message = String.format("Category with id = %s was not found", validId);
        NotFoundException exception = new NotFoundException(message);

        doThrow(exception).when(service).delete(validId);

        mockMvc.perform(delete("/admin/categories/" + validId))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.status", is(HttpStatus.NOT_FOUND.name())),
                        jsonPath("$.reason", is("The required object was not found.")),
                        jsonPath("$.message", is(message)),
                        jsonPath("$.timestamp", is(near(LocalDateTime.now(), DATE_PATTERN)))
                );
    }

    @Test
    void request_withDeleteEndPointAndConflictExceptionThrownByServiceDeleteMethod_shouldReturnStatusConflictAndApiError()
            throws Exception {
        String message = "The category is not empty";
        ConflictException exception = new ConflictException(message);

        doThrow(exception).when(service).delete(validId);

        mockMvc.perform(delete("/admin/categories/" + validId))
                .andExpectAll(
                        status().isConflict(),
                        jsonPath("$.status", is(HttpStatus.CONFLICT.name())),
                        jsonPath("$.reason", is("For the requested operation the conditions are not " +
                                "met.")),
                        jsonPath("$.message", is(message)),
                        jsonPath("$.timestamp", is(near(LocalDateTime.now(), DATE_PATTERN)))
                );
    }

    @Test
    void request_withDeleteEndPoint_shouldReturnStatusNoContentAndInvokeServiceDeleteMethod() throws Exception {
        mockMvc.perform(delete("/admin/categories/" + validId))
                .andExpect(status().isNoContent());

        verify(service, times(1)).delete(validId);
    }
}