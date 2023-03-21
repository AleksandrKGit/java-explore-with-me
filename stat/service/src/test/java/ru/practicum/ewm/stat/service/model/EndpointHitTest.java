package ru.practicum.ewm.stat.service.model;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import java.time.LocalDateTime;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class EndpointHitTest {
    Long id = 1L;
    String app = "appName";
    String uri = "https://ya.ru";
    String ip = "192.168.0.1";
    LocalDateTime timestamp = LocalDateTime.now();

    @SuppressWarnings("all")
    @Test
    void equals_withSameObjectWithNullIdAndOtherFields_shouldReturnTrue() {
        EndpointHit endpointHit = createEndpointHit(null, null, null, null, null);

        assertThat(endpointHit.equals(endpointHit), is(true));
    }

    @SuppressWarnings("all")
    @Test
    void equals_withNullAndNullIdAndOtherFields_shouldReturnFalse() {
        EndpointHit endpointHit = createEndpointHit(null, null, null, null, null);

        assertThat(endpointHit.equals(null), is(false));
    }

    @SuppressWarnings("all")
    @Test
    void equals_withObjectOfOtherClassWithNotNullIdAndNullOtherFields_shouldReturnFalse() {
        EndpointHit endpointHit = createEndpointHit(id, null, null, null, null);
        OtherObject otherObject = new OtherObject(id);

        assertThat(endpointHit.equals(otherObject), is(false));
    }

    @Test
    void equals_withNullIdsAndNotNullEqualOtherFields_shouldReturnFalse() {
        EndpointHit endpointHit1 = createEndpointHit(null, app, uri, ip, timestamp);
        EndpointHit endpointHit2 = createEndpointHit(null, app, uri, ip, timestamp);

        assertThat(endpointHit1.equals(endpointHit2), is(false));
    }

    @Test
    void equals_withNotNullEqualIdsAndNotEqualOtherFields_shouldReturnTrue() {
        EndpointHit endpointHit1 = createEndpointHit(id, null, null, null, null);
        EndpointHit endpointHit2 = createEndpointHit(id, app, uri, ip, timestamp);

        assertThat(endpointHit1.equals(endpointHit2), is(true));
    }

    @ParameterizedTest(name = "ids={0}")
    @NullSource
    @ValueSource(longs = {1L})
    void hashCode_ofTwoEndpointHitsWithEqualIdsAndNotEqualOtherFields_shouldBeEqual(Long id) {
        EndpointHit endpointHit1 = createEndpointHit(id, app, uri, ip, timestamp);
        EndpointHit endpointHit2 = createEndpointHit(id, null, null, null, null);

        assertThat(endpointHit1.hashCode(), equalTo(endpointHit2.hashCode()));
    }

    @Test
    void hashCode_ofTwoEndpointHitsWithNullAndZeroIdsAndNotEqualOtherFields_shouldBeEqual() {
        EndpointHit endpointHit1 = createEndpointHit(null, app, uri, ip, timestamp);
        EndpointHit endpointHit2 = createEndpointHit(0L, null, null, null, null);

        assertThat(endpointHit1.hashCode(), equalTo(endpointHit2.hashCode()));
    }

    @ParameterizedTest(name = "id1={0}, id2=2")
    @NullSource
    @ValueSource(longs = {1L})
    void hashCode_ofTwoEndpointHitsWithNotEqualIdsAndEqualOtherFields_shouldNotBeEqual(Long id) {
        EndpointHit endpointHit1 = createEndpointHit(id, app, uri, ip, timestamp);
        EndpointHit endpointHit2 = createEndpointHit(2L, app, uri, ip, timestamp);

        assertThat(endpointHit1.hashCode(), not(equalTo(endpointHit2.hashCode())));
    }

    EndpointHit createEndpointHit(Long id, String app, String uri, String ip, LocalDateTime timestamp) {
        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setId(id);
        endpointHit.setApp(app);
        endpointHit.setUri(uri);
        endpointHit.setIp(ip);
        endpointHit.setTimestamp(timestamp);
        return endpointHit;
    }

    @SuppressWarnings("all")
    static class OtherObject {
        private Long id;

        public void setId(Long id) {
            this.id = id;
        }

        public Long getId() {
            return id;
        }

        public OtherObject(Long id) {
            this.id = id;
        }
    }
}