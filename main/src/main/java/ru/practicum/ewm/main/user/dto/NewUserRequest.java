package ru.practicum.ewm.main.user.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewUserRequest {
    static final int MAX_NAME_SIZE = 255;

    static final int MAX_EMAIL_SIZE = 255;

    @Size(max = MAX_NAME_SIZE, message = "size must be less or equal to: " + MAX_NAME_SIZE)
    @NotBlank(message = "must not be blank")
    String name;

    @NotNull(message = "must not be null")
    @Size(max = MAX_EMAIL_SIZE, message = "size must be less or equal to: " + MAX_EMAIL_SIZE)
    @Email(message = "must have correct email format",
            regexp = "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$")
    String email;
}