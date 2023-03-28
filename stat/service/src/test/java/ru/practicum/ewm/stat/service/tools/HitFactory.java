package ru.practicum.ewm.stat.service.tools;

import ru.practicum.ewm.stat.dto.ViewStats;
import ru.practicum.ewm.stat.service.model.Hit;
import ru.practicum.ewm.stat.service.tools.matchers.DateMatcher;

import java.time.LocalDateTime;
import java.util.Objects;

public class HitFactory {
    public static Hit createHit(Long id, String app, String uri, String ip, LocalDateTime timestamp) {
        Hit hit = new Hit();

        hit.setId(id);
        hit.setApp(app);
        hit.setUri(uri);
        hit.setIp(ip);
        hit.setTimestamp(timestamp);

        return hit;
    }

    public static ViewStats createHitDto(String app, String uri, Long hits) {
        ViewStats dto = new ViewStats();

        dto.setApp(app);
        dto.setUri(uri);
        dto.setHits(hits);

        return dto;
    }

    public static Hit copyOf(Hit hit) {
        if (hit == null) {
            return null;
        }

        Hit copy = new Hit();

        copy.setId(hit.getId());
        copy.setApp(hit.getApp());
        copy.setUri(hit.getUri());
        copy.setIp(hit.getIp());
        copy.setTimestamp(hit.getTimestamp());

        return copy;
    }

    public static boolean equals(Hit hit1, Hit hit2) {
        if (hit1 == null && hit2 == null) {
            return true;
        }

        return hit1 != null && hit2 != null
                && Objects.equals(hit1.getId(), hit2.getId())
                && Objects.equals(hit1.getApp(), hit2.getApp())
                && Objects.equals(hit1.getUri(), hit2.getUri())
                && Objects.equals(hit1.getIp(), hit2.getIp())
                && DateMatcher.near(hit1.getTimestamp(), hit2.getTimestamp());
    }

    public static boolean equals(ViewStats dto1, ViewStats dto2) {
        if (dto1 == null && dto2 == null) {
            return true;
        }

        return dto1 != null && dto2 != null
                && Objects.equals(dto1.getApp(), dto2.getApp())
                && Objects.equals(dto1.getUri(), dto2.getUri())
                && Objects.equals(dto1.getHits(), dto2.getHits());
    }
}