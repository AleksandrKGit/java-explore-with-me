package ru.practicum.ewm.stat.client;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.ewm.stat.dto.EndpointHit;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import static ru.practicum.ewm.common.support.DateFactory.*;

public class StatClient {
    protected final RestTemplate restTemplate;

    public StatClient(String statServiceUrl, RestTemplateBuilder builder) {
        restTemplate = builder.uriTemplateHandler(new DefaultUriBuilderFactory(statServiceUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    public ResponseEntity<String> hit(EndpointHit endpointHit) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(endpointHit, headers);

        return makeAndSendRequest(HttpMethod.POST, "/hit", request, null);
    }

    public ResponseEntity<String> stats(@Nullable LocalDateTime start, @Nullable LocalDateTime end, String[] uris,
                                        @Nullable Boolean unique) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Object> request = new HttpEntity<>(null, headers);

        QueryParameters queryParameters = new QueryParameters();
        queryParameters.add("start", ofDate(start));
        queryParameters.add("end", ofDate(end));
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
            response = new ResponseEntity<>(e.getResponseBodyAsString(), e.getStatusCode());
        }

        return response;
    }
}