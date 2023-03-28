package ru.practicum.ewm.main.request.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.main.event.model.Event;
import ru.practicum.ewm.main.user.User;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "requests", uniqueConstraints = { @UniqueConstraint(columnNames = { "event_id", "requestor_id" }) })
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, updatable = false, foreignKey = @ForeignKey(name = "fk_request_requestor"))
    User requestor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, updatable = false, foreignKey = @ForeignKey(name = "fk_request_event"))
    Event event;

    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
    LocalDateTime created;

    @Enumerated(EnumType.STRING)
    RequestStatus status;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Request)) {
            return false;
        }

        return id != null && id.equals(((Request) obj).getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}