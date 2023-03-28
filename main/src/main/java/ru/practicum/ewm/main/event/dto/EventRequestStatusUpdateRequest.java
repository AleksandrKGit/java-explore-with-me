package ru.practicum.ewm.main.event.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.main.validation.constraints.RequestUpdateStatus;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventRequestStatusUpdateRequest {
    @RequestUpdateStatus
    String status;

    @Size(min = 1, message = "must not be empty")
    @NotNull(message = "must not be null")
    List<Long> requestIds;
}
