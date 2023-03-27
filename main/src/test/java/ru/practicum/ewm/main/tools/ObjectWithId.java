package ru.practicum.ewm.main.tools;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
public class ObjectWithId {
    private Long id;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof ObjectWithId)) {
            return false;
        }

        return id != null && id.equals(((ObjectWithId) obj).getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}