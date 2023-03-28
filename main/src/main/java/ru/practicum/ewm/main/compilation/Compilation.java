package ru.practicum.ewm.main.compilation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.main.event.model.Event;
import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "compilations")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, unique = true)
    String title;

    @Column(nullable = false)
    Boolean pinned;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @OrderBy("eventDate DESC")
    @JoinTable(name = "compilation_events",
            joinColumns = @JoinColumn(name = "compilation_id", nullable = false, updatable = false),
            inverseJoinColumns = @JoinColumn(name = "event_id", nullable = false, updatable = false),
            foreignKey = @ForeignKey(name = "fk_compilation_events_compilation"),
            inverseForeignKey = @ForeignKey(name = "fk_compilation_events_event"))
    List<Event> events;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Compilation)) {
            return false;
        }

        return id != null && id.equals(((Compilation) obj).getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}