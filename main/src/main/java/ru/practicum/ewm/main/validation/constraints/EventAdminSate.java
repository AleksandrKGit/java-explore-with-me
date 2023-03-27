package ru.practicum.ewm.main.validation.constraints;

import ru.practicum.ewm.main.validation.validators.EventAdminSateValidator;
import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(ElementType.FIELD)
@Retention(RUNTIME)
@Constraint(validatedBy = EventAdminSateValidator.class)
@Documented
public @interface EventAdminSate {
    String message() default "mast be one of allowed";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}