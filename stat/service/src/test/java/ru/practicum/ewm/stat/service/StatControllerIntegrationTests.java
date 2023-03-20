package ru.practicum.ewm.stat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.ewm.stat.dto.EndpointHitDto;
import ru.practicum.ewm.stat.service.model.EndpointHit;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatControllerIntegrationTests {
    MockMvc mockMvc;

    @Autowired
    StatController controller;

    @Autowired
    EntityManager em;

    @Autowired
    ObjectMapper objectMapper;

    static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    void hit_shouldReturnStatusOkAndSaveEndpointHit() throws Exception {
        EndpointHitDto validEndpointHitDto = new EndpointHitDto();
        validEndpointHitDto.setApp("appName");
        validEndpointHitDto.setUri("https://ya.ru");
        validEndpointHitDto.setIp("192.168.0.1");
        validEndpointHitDto.setTimestamp("2023-03-12 00:00:00");

        mockMvc.perform(post("/hit")
                        .content(objectMapper.writeValueAsString(validEndpointHitDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        EndpointHit createdHit = em.createQuery("Select eh from EndpointHit eh", EndpointHit.class)
                .getSingleResult();

        assertThat(createdHit, allOf(
                hasProperty("id", is(notNullValue())),
                hasProperty("app", equalTo(validEndpointHitDto.getApp())),
                hasProperty("uri", equalTo(validEndpointHitDto.getUri())),
                hasProperty("ip", equalTo(validEndpointHitDto.getIp())),
                hasProperty("timestamp", equalTo(LocalDateTime.parse(validEndpointHitDto.getTimestamp(),
                        DateTimeFormatter.ofPattern(DATE_PATTERN))))
        ));
    }

    static EndpointHit createEndpointHit(Long id, String app, String uri, String ip, LocalDateTime timestamp) {
        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setId(id);
        endpointHit.setApp(app);
        endpointHit.setUri(uri);
        endpointHit.setIp(ip);
        endpointHit.setTimestamp(timestamp);
        return endpointHit;
    }

    @Test
    void stats_shouldReturnDtoListOfViewStats() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.minusDays(30);
        LocalDateTime end = now.minusDays(10);
        String[] uris = new String[] {"uri1", "uri2"};
        LocalDateTime timestamp = start.plusDays(1);
        String app1 = "app1";
        String app2 = "app2";
        String ip1 = "192.168.0.1";
        String ip2 = "192.168.0.2";
        String ip3 = "192.168.0.3";
        EndpointHit endpointHitOtherUri = createEndpointHit(null, app1, "otherUri", ip1, timestamp);
        EndpointHit endpointHitBefore = createEndpointHit(null, app1, uris[0], ip1, start.minusDays(1));
        EndpointHit endpointHitAfter = createEndpointHit(null, app1, uris[0], ip1, end.plusDays(1));
        EndpointHit endpointHitApp2Uri0Ip1 = createEndpointHit(null, app2, uris[0], ip1, timestamp);
        EndpointHit endpointHitApp1Uri0Ip11 = createEndpointHit(null, app1, uris[0], ip1, timestamp);
        EndpointHit endpointHitApp1Uri0DuplicateIp = createEndpointHit(null, app1, uris[0], ip1, timestamp);
        EndpointHit endpointHitApp1Uri0Ip2 = createEndpointHit(null, app1, uris[0], ip2, timestamp);
        EndpointHit endpointHitApp1Uri1Ip1 = createEndpointHit(null, app1, uris[1], ip1, timestamp);
        EndpointHit endpointHitApp1Uri1Ip2 = createEndpointHit(null, app1, uris[1], ip2, timestamp.plusDays(1));
        EndpointHit endpointHitApp1Uri1Ip3 = createEndpointHit(null, app1, uris[1], ip3, timestamp.plusDays(2));
        em.persist(endpointHitOtherUri);
        em.persist(endpointHitBefore);
        em.persist(endpointHitAfter);
        em.persist(endpointHitApp2Uri0Ip1);
        em.persist(endpointHitApp1Uri0Ip11);
        em.persist(endpointHitApp1Uri0DuplicateIp);
        em.persist(endpointHitApp1Uri0Ip2);
        em.persist(endpointHitApp1Uri1Ip1);
        em.persist(endpointHitApp1Uri1Ip2);
        em.persist(endpointHitApp1Uri1Ip3);
        em.flush();

        mockMvc.perform(get("/stats?start=" + start.format(DateTimeFormatter.ofPattern(DATE_PATTERN))
                        + "&end=" + end.format(DateTimeFormatter.ofPattern(DATE_PATTERN))
                        + "&uris=" + String.join(",", uris) + "&unique=true"))
                .andExpectAll(status().isOk(),
                        jsonPath("$[0].app", equalTo(app1)),
                        jsonPath("$[0].uri", equalTo(uris[1])),
                        jsonPath("$[0].hits", equalTo(3L), Long.class),
                        jsonPath("$[1].app", equalTo(app1)),
                        jsonPath("$[1].uri", equalTo(uris[0])),
                        jsonPath("$[1].hits", equalTo(2L), Long.class),
                        jsonPath("$[2].app", equalTo(app2)),
                        jsonPath("$[2].uri", equalTo(uris[0])),
                        jsonPath("$[2].hits", equalTo(1L), Long.class)
                );
    }
}