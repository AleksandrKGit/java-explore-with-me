package ru.practicum.ewm.stat.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;
import org.mockserver.model.MediaType;
import org.mockserver.model.Parameter;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.ewm.stat.dto.EndpointHitDto;
import ru.practicum.ewm.stat.dto.ViewStatsDto;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasProperty;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.matchers.Times.exactly;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.JsonBody.json;

@FieldDefaults(level = AccessLevel.PRIVATE)
class StatClientTest {
    static ClientAndServer mockStatServer;

    static final Integer serverPort = 9090;

    static String serverHost;

    static StatClient client;

    static ObjectMapper objectMapper;

    @BeforeAll
    static void startServer() {
        mockStatServer = startClientAndServer(serverPort);
        serverHost = mockStatServer.remoteAddress().getHostName();
        client = new StatClient("http://" + serverHost + ":" + serverPort, new RestTemplateBuilder());
        objectMapper = new ObjectMapper();
    }

    @AfterAll
    static void stopServer() {
        mockStatServer.stop();
    }

    @Test
    void hit_shouldReturnResultOfHitRequestToStatServer() {
        EndpointHitDto endpointHitDto = new EndpointHitDto();
        endpointHitDto.setApp("appName");
        endpointHitDto.setUri("https://ya.ru");
        endpointHitDto.setIp("192.168.0.1");
        endpointHitDto.setTimestamp("2023-03-12 00:00:00");
        mockStatServer.when(
                        request()
                                .withMethod("POST")
                                .withHeader(new Header("Content-type", "application/json"))
                                .withBody(json(endpointHitDto))
                                .withPath("/hits"),
                        exactly(1)
                )
                .respond(
                        response()
                                .withStatusCode(HttpStatus.CREATED.value())
                );

        ResponseEntity<String> response = client.hit(endpointHitDto);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.CREATED));
    }

    final static String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    @Test
    void stats_shouldReturnResultOfStatsRequestToStatServer() throws Exception {
        List<ViewStatsDto> viewStatsDtoList = List.of(new ViewStatsDto("appName", "https://ya.ru", 1L));
        String startStr = "2023-03-10 00:00:00";
        String endStr = "2023-03-12 00:00:00";
        LocalDateTime start = LocalDateTime.parse(startStr, DateTimeFormatter.ofPattern(DATE_PATTERN));
        LocalDateTime end = LocalDateTime.parse(endStr, DateTimeFormatter.ofPattern(DATE_PATTERN));
        String[] uris = new String[] {"https://ya.ru"};
        Boolean unique = false;
        mockStatServer.when(
                        request()
                                .withMethod("GET")
                                .withQueryStringParameters(List.of(
                                        Parameter.param("start", startStr),
                                        Parameter.param("end", endStr),
                                        Parameter.param("uris", uris),
                                        Parameter.param("unique", unique.toString())
                                ))
                                .withPath("/stats"),
                        exactly(1)
                )
                .respond(
                        response()
                                .withContentType(MediaType.APPLICATION_JSON)
                                .withStatusCode(HttpStatus.OK.value())
                                .withBody(json(objectMapper.writeValueAsString(viewStatsDtoList)))
                );

        ResponseEntity<String> response = client.stats(start, end, uris, unique);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));

        List<ViewStatsDto> result = objectMapper.readValue(response.getBody(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, ViewStatsDto.class));

        assertThat(result, contains(viewStatsDtoList.stream().map(dto -> allOf(
                hasProperty("app", equalTo(dto.getApp())),
                hasProperty("uri", equalTo(dto.getUri())),
                hasProperty("hits", equalTo(dto.getHits()))
        )).collect(Collectors.toList())));
    }
}