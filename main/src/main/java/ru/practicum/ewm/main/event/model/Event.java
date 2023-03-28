package ru.practicum.ewm.main.event.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.main.category.Category;
import ru.practicum.ewm.main.compilation.Compilation;
import ru.practicum.ewm.main.user.User;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "events")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, updatable = false, foreignKey = @ForeignKey(name = "fk_event_initiator"))
    User initiator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, foreignKey = @ForeignKey(name = "fk_event_category"))
    Category category;

    @Column(nullable = false, length = 120)
    String title;

    @Column(nullable = false, length = 2000)
    String annotation;

    @Column(nullable = false, length = 7000)
    String description;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "lat", column = @Column(name = "location_lat", nullable = false)),
            @AttributeOverride(name = "lon", column = @Column(name = "location_lon", nullable = false))
    })
    Location location;

    @Column(nullable = false)
    Boolean paid;

    @Column(nullable = false)
    Boolean requestModeration;

    @Column(nullable = false)
    Integer participantLimit;

    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
    LocalDateTime createdOn;

    @Column(columnDefinition = "TIMESTAMP")
    LocalDateTime publishedOn;

    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    LocalDateTime eventDate;

    @Enumerated(EnumType.STRING)
    EventState state;

    @Column(nullable = false)
    Long confirmedRequests = 0L;

    @ManyToMany(mappedBy = "events", fetch = FetchType.LAZY)
    List<Compilation> compilations;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Event)) {
            return false;
        }

        return id != null && id.equals(((Event) obj).getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}