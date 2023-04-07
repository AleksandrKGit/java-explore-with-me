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
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.practicum.ewm.main.exception.NotFoundException;
import ru.practicum.ewm.main.tools.configuration.AppTestConfiguration;
import ru.practicum.ewm.main.category.dto.CategoryDto;
import ru.practicum.ewm.main.category.service.CategoryService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.ewm.main.tools.factories.CategoryFactory.createCategoryDto;
import static ru.practicum.ewm.main.tools.matchers.LocalDateTimeMatcher.near;

@WebMvcTest(CategoryPublicController.class)
@SpringJUnitConfig({AppTestConfiguration.class})
@FieldDefaults(level = AccessLevel.PRIVATE)
class CategoryPublicControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    CategoryService service;

    static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    static final long validId = 10L;

    final CategoryDto resultDto = createCategoryDto(1L, "category1");

    final List<CategoryDto> resultListDto = List.of(resultDto);

    private static Stream<Arguments> badRequest() {
        String incorrectId = "a";
        String incorrectFrom = "a";
        String incorrectSize = "a";

        return Stream.of(
                Arguments.of("read with incorrect from",
                        get("/categories?from=" + incorrectFrom)),

                Arguments.of("read with incorrect size",
                        get("/categories?size=" + incorrectSize)),

                Arguments.of("readById with incorrect Id",
                        get("/categories/" + incorrectId))
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

        return Stream.of(
                Arguments.of("read with invalid from",
                        get("/categories?from=" + invalidFrom),
                        "Field: from. Error: must not be less than 0. Value: -1"),

                Arguments.of("read with invalid size",
                        get("/categories?size=" + invalidSize),
                        "Field: size. Error: must not be less than 1. Value: 0")
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
    void request_withReadEndPointWithParams_shouldReturnStatusOkAndServiceReadMethodResultInvokedWithParams()
            throws Exception {
        Integer from = 1;
        Integer size = 2;

        when(service.find(from, size)).thenReturn(resultListDto);

        mockMvc.perform(get(String.format("/categories?from=%s&size=%s", from, size)))
                .andExpect(content().json(objectMapper.writeValueAsString(resultListDto)))
                .andExpect(status().isOk());
    }

    @Test
    void request_withReadEndPointWithoutParams_shouldReturnStatusOkAndServiceReadMethodResultInvokedWithDefaultParams()
            throws Exception {
        when(service.find(0, 10)).thenReturn(resultListDto);

        mockMvc.perform(get("/categories"))
                .andExpect(content().json(objectMapper.writeValueAsString(resultListDto)))
                .andExpect(status().isOk());
    }

    @Test
    void request_withReadByIdEndPointAndNotFoundExceptionThrownByServiceDeleteMethod_shouldReturnStatusNotFoundAndApiError()
            throws Exception {
        String message = String.format("Category with id = %s was not found", validId);
        NotFoundException exception = new NotFoundException(message);

        when(service.get(validId)).thenThrow(exception);

        mockMvc.perform(get("/categories/" + validId))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.status", is(HttpStatus.NOT_FOUND.name())),
                        jsonPath("$.reason", is("The required object was not found.")),
                        jsonPath("$.message", is(message)),
                        jsonPath("$.timestamp", is(near(LocalDateTime.now(), DATE_PATTERN)))
                );
    }

    @Test
    void request_withReadByIdEndPoint_shouldReturnStatusOkAndServiceReadByIdMethodResult()
            throws Exception {
        when(service.get(validId)).thenReturn(resultDto);

        mockMvc.perform(get("/categories/" + validId))
                .andExpect(content().json(objectMapper.writeValueAsString(resultDto)))
                .andExpect(status().isOk());
    }
}