package ru.practicum.ewm.main.validation.constraints;

import ru.practicum.ewm.main.validation.validators.EventStateValidator;
import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RUNTIME)
@Constraint(validatedBy = EventStateValidator.class)
@Documented
public @interface EventState {
    String message() default "mast be one of allowed";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}