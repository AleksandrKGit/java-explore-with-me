package ru.practicum.ewm.stat.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.practicum.ewm.stat.dto.EndpointHit;
import ru.practicum.ewm.stat.dto.ViewStats;
import ru.practicum.ewm.stat.service.service.StatService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static ru.practicum.ewm.stat.service.tools.matchers.LocalDateTimeMatcher.near;

@WebMvcTest(StatController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class StatControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    StatService service;

    @Autowired
    ObjectMapper objectMapper;

    static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    static final String validHitJson = "{\"app\":\"appName\",\"uri\":\"https://ya.ru\",\"ip\":\"192.168.0.1\"," +
            "\"timestamp\":\"2023-03-10 00:00:00\"}";

    static final String validStart = "2023-03-10 00:00:00";

    static final String validEnd = "2023-03-12 00:00:00";

    static final String validUris = "https://ya.ru";

    private static Stream<Arguments> badRequest() {
        String incorrectJson = "}";
        String incorrectDate = "q";
        String incorrectUnique = "q";

        return Stream.of(
                Arguments.of("hit without media type",
                        post("/hit")
                                .content(validHitJson)),

                Arguments.of("hit with incorrect media type",
                        post("/hit")
                                .content(validHitJson)
                                .contentType(MediaType.IMAGE_PNG)),

                Arguments.of("hit with incorrect json",
                        post("/hit")
                                .content(incorrectJson)
                                .contentType(MediaType.APPLICATION_JSON)),

                Arguments.of("stats without uris",
                        get("/stats?start=" + validStart + "&end=" + validEnd)),

                Arguments.of("stats without start",
                        get("/stats?end=" + validEnd + "&uris=" + validUris)),

                Arguments.of("stats with incorrect start",
                        get("/stats?start=" + incorrectDate + "&end=" + validEnd + "&uris=" + validUris)),

                Arguments.of("stats without end",
                        get("/stats?start=" + validStart + "&uris=" + validUris)),

                Arguments.of("stats with incorrect end",
                        get("/stats?start=" + validStart + "&end=" + incorrectDate + "&uris=" + validUris)),

                Arguments.of("stats with incorrect unique",
                        get("/stats?start=" + validStart + "&end=" + validEnd + "&uris=" + validUris
                                + "&unique=" + incorrectUnique))
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("badRequest")
    void request_withIncorrectDataOrMediaType_shouldReturnBadRequest(String testName, MockHttpServletRequestBuilder request) throws Exception {
        mockMvc.perform(request).andExpect(status().isBadRequest());
    }

    @Test
    void request_withHitEndPoint_shouldReturnStatusCreatedAndInvokeServiceCreateMethod() throws Exception {
        EndpointHit validEndpointHit = new EndpointHit();
        validEndpointHit.setApp("appName");
        validEndpointHit.setUri("https://ya.ru");
        validEndpointHit.setIp("192.168.0.1");
        validEndpointHit.setTimestamp("2023-03-12 00:00:00");
        ArgumentCaptor<EndpointHit> endpointHitDtoArgumentCaptor = ArgumentCaptor.forClass(EndpointHit.class);
        doNothing().when(service).create(endpointHitDtoArgumentCaptor.capture());

        mockMvc.perform(post("/hit")
                        .content(objectMapper.writeValueAsString(validEndpointHit))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        EndpointHit resultEndpointHit = endpointHitDtoArgumentCaptor.getValue();
        assertThat(resultEndpointHit, allOf(
                hasProperty("app", equalTo(validEndpointHit.getApp())),
                hasProperty("uri", equalTo(validEndpointHit.getUri())),
                hasProperty("ip", equalTo(validEndpointHit.getIp())),
                hasProperty("timestamp", equalTo(validEndpointHit.getTimestamp()))
        ));
    }

    @Test
    void request_withStatsEndPointAndRuntimeExceptionThrownByServiceCreateMethod_shouldReturnStatusInternalServerErrorAndApiError()
            throws Exception {
        String message = "exceptionMessage";
        RuntimeException exception = new RuntimeException(message);
        LocalDateTime start = LocalDateTime.parse(validStart, DateTimeFormatter.ofPattern(DATE_PATTERN));
        LocalDateTime end = LocalDateTime.parse(validEnd, DateTimeFormatter.ofPattern(DATE_PATTERN));
        ArgumentCaptor<String[]> urisArgumentCaptor = ArgumentCaptor.forClass(String[].class);
        String[] uris = new String[] {validUris};
        when(service.find(eq(start), eq(end), urisArgumentCaptor.capture(), eq(false)))
                .thenThrow(exception);

        mockMvc.perform(get("/stats?start=" + validStart + "&end=" + validEnd + "&uris=" + validUris))
                .andExpectAll(
                        status().isInternalServerError(),
                        jsonPath("$.status", is(HttpStatus.INTERNAL_SERVER_ERROR.name())),
                        jsonPath("$.reason", is("Stat service error.")),
                        jsonPath("$.message", is(message)),
                        jsonPath("$.timestamp", is(near(LocalDateTime.now())))
                );

        String[] requestUris = urisArgumentCaptor.getValue();
        assertThat(requestUris, arrayContainingInAnyOrder(uris));
    }

    @Test
    void request_withStatsEndPoint_shouldReturnStatusOkAndServiceCreateMethodResult() throws Exception {
        List<ViewStats> viewStatsList = List.of(new ViewStats("appName", "https://ya.ru", 1L));
        LocalDateTime start = LocalDateTime.parse(validStart, DateTimeFormatter.ofPattern(DATE_PATTERN));
        LocalDateTime end = LocalDateTime.parse(validEnd, DateTimeFormatter.ofPattern(DATE_PATTERN));
        ArgumentCaptor<String[]> urisArgumentCaptor = ArgumentCaptor.forClass(String[].class);
        String[] uris = new String[] {validUris};
        when(service.find(eq(start), eq(end), urisArgumentCaptor.capture(), eq(false)))
                .thenReturn(viewStatsList);

        mockMvc.perform(get("/stats?start=" + validStart + "&end=" + validEnd + "&uris=" + validUris))
                .andExpect(content().json(objectMapper.writeValueAsString(viewStatsList)))
                .andExpect(status().isOk());

        String[] requestUris = urisArgumentCaptor.getValue();
        assertThat(requestUris, arrayContainingInAnyOrder(uris));
    }
}