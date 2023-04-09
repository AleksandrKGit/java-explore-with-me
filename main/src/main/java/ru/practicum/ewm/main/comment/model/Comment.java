package ru.practicum.ewm.main.comment.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.main.entity.EntityWithId;
import ru.practicum.ewm.main.event.model.Event;
import ru.practicum.ewm.main.user.User;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "comments")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment  implements EntityWithId {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, updatable = false, foreignKey = @ForeignKey(name = "fk_comment_commentator"))
    User commentator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, updatable = false, foreignKey = @ForeignKey(name = "fk_comment_event"))
    Event event;

    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
    LocalDateTime createdOn;

    @Column(nullable = false, length = 2000)
    String text;

    @Enumerated(EnumType.STRING)
    CommentState state;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Comment)) {
            return false;
        }

        return id != null && id.equals(((Comment) obj).getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}