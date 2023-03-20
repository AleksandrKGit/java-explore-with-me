package ru.practicum.ewm.stat.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.ewm.stat.dto.ViewStatsDto;
import ru.practicum.ewm.stat.service.model.EndpointHit;
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
    @Autowired
    TestEntityManager em;

    @Autowired
    StatRepository repository;

    static final String app = "appName";

    static final String uri = "https://ya.ru";

    static final String ip = "192.168.0.1";

    static final LocalDateTime timestamp = LocalDateTime.now();

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
    void save_withWithNullId_shouldReturnAttachedTransferredBookingWithGeneratedId() {
        EndpointHit endpointHit = createEndpointHit(null, app, uri, ip, timestamp);

        EndpointHit savedEndpointHit = repository.saveAndFlush(endpointHit);

        assertThat(savedEndpointHit == endpointHit, is(true));
        assertThat(savedEndpointHit, hasProperty("id", is(not(nullValue()))));
    }

    @Test
    void save_withWithNotExistingId_shouldReturnNewAttachedBookingWithGeneratedId() {
        Long notExistingId = 1000L;
        EndpointHit endpointHit = createEndpointHit(notExistingId, app, uri, ip, timestamp);

        EndpointHit savedEndpointHit = repository.saveAndFlush(endpointHit);

        assertThat(endpointHit, hasProperty("id", is(notNullValue())));
        assertThat(savedEndpointHit == endpointHit, is(false));
        assertThat(savedEndpointHit, allOf(
                hasProperty("id", not(nullValue())),
                hasProperty("id", not(equalTo(endpointHit.getId()))),
                hasProperty("app", equalTo(endpointHit.getApp())),
                hasProperty("uri", equalTo(endpointHit.getUri())),
                hasProperty("ip", equalTo(endpointHit.getIp())),
                hasProperty("timestamp", equalTo(endpointHit.getTimestamp()))
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
        EndpointHit endpointHit = createEndpointHit(null, app, uri, ip, timestamp);

        assertThrows(Exception.class, () -> repository.saveAndFlush(endpointHit));
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
        EndpointHit endpointHitOtherUri = createEndpointHit(null, app1, "otherUri", ip1, timestamp);
        EndpointHit endpointHitBefore = createEndpointHit(null, app1, uris[0], ip1, start.minusDays(1));
        EndpointHit endpointHitAfter = createEndpointHit(null, app1, uris[0], ip1, end.plusDays(1));
        EndpointHit endpointHitApp2Uri0Ip1 = createEndpointHit(null, app2, uris[0], ip1, timestamp);
        EndpointHit endpointHitApp1Uri0Ip11 = createEndpointHit(null, app1, uris[0], ip1, timestamp);
        EndpointHit endpointHitApp1Uri0Ip12 = createEndpointHit(null, app1, uris[0], ip1, timestamp.plusDays(1));
        EndpointHit endpointHitApp1Uri0Ip2 = createEndpointHit(null, app1, uris[0], ip2, timestamp);
        EndpointHit endpointHitApp1Uri1Ip1 = createEndpointHit(null, app1, uris[1], ip1, timestamp);
        EndpointHit endpointHitApp1Uri1Ip2 = createEndpointHit(null, app1, uris[1], ip2, timestamp.plusDays(1));
        em.persist(endpointHitOtherUri);
        em.persist(endpointHitBefore);
        em.persist(endpointHitAfter);
        em.persist(endpointHitApp2Uri0Ip1);
        em.persist(endpointHitApp1Uri0Ip11);
        em.persist(endpointHitApp1Uri0Ip12);
        em.persist(endpointHitApp1Uri0Ip2);
        em.persist(endpointHitApp1Uri1Ip1);
        em.persist(endpointHitApp1Uri1Ip2);
        em.flush();
        List<ViewStatsDto> result = List.of(
                new ViewStatsDto(app1, uris[0], 3L),
                new ViewStatsDto(app1, uris[1], 2L),
                new ViewStatsDto(app2, uris[0], 1L)
        );

        List<ViewStatsDto> target = repository.find(start, end, uris);

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
        List<ViewStatsDto> result = List.of(
                new ViewStatsDto(app1, uris[1], 3L),
                new ViewStatsDto(app1, uris[0], 2L),
                new ViewStatsDto(app2, uris[0], 1L)
        );

        List<ViewStatsDto> target = repository.findUnique(start, end, uris);

        assertThat(target, contains(result.stream().map(dto -> allOf(
                hasProperty("app", equalTo(dto.getApp())),
                hasProperty("uri", equalTo(dto.getUri())),
                hasProperty("hits", equalTo(dto.getHits()))
        )).collect(Collectors.toList())));
    }
}