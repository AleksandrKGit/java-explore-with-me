package ru.practicum.ewm.stat.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.logging.log4j.util.Strings;
import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.testcontainers.containers.PostgreSQLContainer;
import ru.practicum.ewm.stat.dto.ViewStats;
import ru.practicum.ewm.stat.service.model.Hit;
import ru.practicum.ewm.stat.service.tools.PostgresqlTestContainer;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@DataJpaTest
@FieldDefaults(level = AccessLevel.PRIVATE)
class StatRepositoryTest {
    @ClassRule
    public static PostgreSQLContainer<PostgresqlTestContainer> postgreSQLContainer =
            PostgresqlTestContainer.getInstance();

    @Autowired
    TestEntityManager em;

    @Autowired
    StatRepository repository;

    static final String app = "appName";

    static final String uri = "https://ya.ru";

    static final String ip = "192.168.0.1";

    static final LocalDateTime timestamp = LocalDateTime.now();

    static Hit createEndpointHit(Long id, String app, String uri, String ip, LocalDateTime timestamp) {
        Hit hit = new Hit();
        hit.setId(id);
        hit.setApp(app);
        hit.setUri(uri);
        hit.setIp(ip);
        hit.setTimestamp(timestamp);
        return hit;
    }

    @Test
    void save_withWithNullId_shouldReturnAttachedTransferredBookingWithGeneratedId() {
        Hit hit = createEndpointHit(null, app, uri, ip, timestamp);

        Hit savedHit = repository.saveAndFlush(hit);

        assertThat(savedHit == hit, is(true));
        assertThat(savedHit, hasProperty("id", is(not(nullValue()))));
    }

    @Test
    void save_withWithNotExistingId_shouldReturnNewAttachedBookingWithGeneratedId() {
        Long notExistingId = 1000L;
        Hit hit = createEndpointHit(notExistingId, app, uri, ip, timestamp);

        Hit savedHit = repository.saveAndFlush(hit);

        assertThat(hit, hasProperty("id", is(notNullValue())));
        assertThat(savedHit == hit, is(false));
        assertThat(savedHit, allOf(
                hasProperty("id", not(nullValue())),
                hasProperty("id", not(equalTo(hit.getId()))),
                hasProperty("app", equalTo(hit.getApp())),
                hasProperty("uri", equalTo(hit.getUri())),
                hasProperty("ip", equalTo(hit.getIp())),
                hasProperty("timestamp", equalTo(hit.getTimestamp()))
        ));
    }

    static Stream<Arguments> incorrectFields() {
        return Stream.of(
                Arguments.of("null app", null, uri, ip, timestamp),
                Arguments.of("big app", Strings.repeat("a", 257), uri, ip, timestamp),
                Arguments.of("null uri", app, null, ip, timestamp),
                Arguments.of("big uri", app, Strings.repeat("a", 2001), ip, timestamp),
                Arguments.of("null ip", app, uri, null, timestamp),
                Arguments.of("big ip", app, uri, Strings.repeat("a", 16), timestamp),
                Arguments.of("null timestamp", app, uri, ip, null)
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("incorrectFields")
    void save_withIncorrectFields_shouldThrowException(String testName, String app, String uri, String ip,
                                                       LocalDateTime timestamp) {
        Hit hit = createEndpointHit(null, app, uri, ip, timestamp);

        assertThrows(Exception.class, () -> repository.saveAndFlush(hit));
    }

    @Test
    void find_shouldReturnListOfViewStatsDtoWithNotUniqueIpHits() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.minusDays(30);
        LocalDateTime end = now.minusDays(10);
        String[] uris = new String[] {"uri1", "uri2"};
        LocalDateTime timestamp = start.plusDays(1);
        String app1 = "app1";
        String app2 = "app2";
        String ip1 = "192.168.0.1";
        String ip2 = "192.168.0.2";
        Hit hitOtherUri = createEndpointHit(null, app1, "otherUri", ip1, timestamp);
        Hit hitBefore = createEndpointHit(null, app1, uris[0], ip1, start.minusDays(1));
        Hit hitAfter = createEndpointHit(null, app1, uris[0], ip1, end.plusDays(1));
        Hit hitApp2Uri0Ip1 = createEndpointHit(null, app2, uris[0], ip1, timestamp);
        Hit hitApp1Uri0Ip11 = createEndpointHit(null, app1, uris[0], ip1, timestamp);
        Hit hitApp1Uri0Ip12 = createEndpointHit(null, app1, uris[0], ip1, timestamp.plusDays(1));
        Hit hitApp1Uri0Ip2 = createEndpointHit(null, app1, uris[0], ip2, timestamp);
        Hit hitApp1Uri1Ip1 = createEndpointHit(null, app1, uris[1], ip1, timestamp);
        Hit hitApp1Uri1Ip2 = createEndpointHit(null, app1, uris[1], ip2, timestamp.plusDays(1));
        em.persist(hitOtherUri);
        em.persist(hitBefore);
        em.persist(hitAfter);
        em.persist(hitApp2Uri0Ip1);
        em.persist(hitApp1Uri0Ip11);
        em.persist(hitApp1Uri0Ip12);
        em.persist(hitApp1Uri0Ip2);
        em.persist(hitApp1Uri1Ip1);
        em.persist(hitApp1Uri1Ip2);
        em.flush();
        List<ViewStats> result = List.of(
                new ViewStats(app1, uris[0], 3L),
                new ViewStats(app1, uris[1], 2L),
                new ViewStats(app2, uris[0], 1L)
        );

        List<ViewStats> target = repository.find(start, end, uris);

        assertThat(target, contains(result.stream().map(dto -> allOf(
                hasProperty("app", equalTo(dto.getApp())),
                hasProperty("uri", equalTo(dto.getUri())),
                hasProperty("hits", equalTo(dto.getHits()))
        )).collect(Collectors.toList())));
    }


    @Test
    void findUnique_shouldReturnListOfViewStatsDtoWithUniqueIpHits() {
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
        Hit hitOtherUri = createEndpointHit(null, app1, "otherUri", ip1, timestamp);
        Hit hitBefore = createEndpointHit(null, app1, uris[0], ip1, start.minusDays(1));
        Hit hitAfter = createEndpointHit(null, app1, uris[0], ip1, end.plusDays(1));
        Hit hitApp2Uri0Ip1 = createEndpointHit(null, app2, uris[0], ip1, timestamp);
        Hit hitApp1Uri0Ip11 = createEndpointHit(null, app1, uris[0], ip1, timestamp);
        Hit hitApp1Uri0DuplicateIp = createEndpointHit(null, app1, uris[0], ip1, timestamp);
        Hit hitApp1Uri0Ip2 = createEndpointHit(null, app1, uris[0], ip2, timestamp);
        Hit hitApp1Uri1Ip1 = createEndpointHit(null, app1, uris[1], ip1, timestamp);
        Hit hitApp1Uri1Ip2 = createEndpointHit(null, app1, uris[1], ip2, timestamp.plusDays(1));
        Hit hitApp1Uri1Ip3 = createEndpointHit(null, app1, uris[1], ip3, timestamp.plusDays(2));
        em.persist(hitOtherUri);
        em.persist(hitBefore);
        em.persist(hitAfter);
        em.persist(hitApp2Uri0Ip1);
        em.persist(hitApp1Uri0Ip11);
        em.persist(hitApp1Uri0DuplicateIp);
        em.persist(hitApp1Uri0Ip2);
        em.persist(hitApp1Uri1Ip1);
        em.persist(hitApp1Uri1Ip2);
        em.persist(hitApp1Uri1Ip3);
        em.flush();
        List<ViewStats> result = List.of(
                new ViewStats(app1, uris[1], 3L),
                new ViewStats(app1, uris[0], 2L),
                new ViewStats(app2, uris[0], 1L)
        );

        List<ViewStats> target = repository.findUnique(start, end, uris);

        assertThat(target, contains(result.stream().map(dto -> allOf(
                hasProperty("app", equalTo(dto.getApp())),
                hasProperty("uri", equalTo(dto.getUri())),
                hasProperty("hits", equalTo(dto.getHits()))
        )).collect(Collectors.toList())));
    }
}