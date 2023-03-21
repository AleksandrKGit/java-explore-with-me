package ru.practicum.ewm.stat.client;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.ewm.stat.dto.EndpointHitDto;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class StatClient {
    protected final RestTemplate restTemplate;

    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public StatClient(String serverUrl, RestTemplateBuilder builder) {
        restTemplate = builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    public ResponseEntity<String> hit(EndpointHitDto endpointHitDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(endpointHitDto, headers);

        return makeAndSendRequest(HttpMethod.POST, "/hits", request, null);
    }

    public ResponseEntity<String> stats(LocalDateTime start, LocalDateTime end, String[] uris, Boolean unique) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Object> request = new HttpEntity<>(null, headers);

        QueryParameters queryParameters = new QueryParameters();
        queryParameters.add("start", start.format(DateTimeFormatter.ofPattern(DATE_PATTERN)));
        queryParameters.add("end", end.format(DateTimeFormatter.ofPattern(DATE_PATTERN)));
        queryParameters.add("uris", uris);
        queryParameters.add("unique", unique);

        return makeAndSendRequest(HttpMethod.GET, "/stats" + queryParameters.getQuery(), request,
                queryParameters.getParameters());
    }

    private <T> ResponseEntity<String> makeAndSendRequest(HttpMethod method, String path, HttpEntity<T> request,
                                                          @Nullable Map<String, Object> parameters) {
        ResponseEntity<String> response;

        try {
            if (parameters != null) {
                response = restTemplate.exchange(path, method, request, String.class, parameters);
            } else {
                response = restTemplate.exchange(path, method, request, String.class);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }

        return prepareResponse(response);
    }

    private ResponseEntity<String> prepareResponse(ResponseEntity<String> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }
}