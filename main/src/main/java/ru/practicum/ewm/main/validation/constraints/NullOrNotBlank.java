package ru.practicum.ewm.main.validation.constraints;

import ru.practicum.ewm.main.validation.validators.NullOrNotBlankValidator;
import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ElementType.FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = NullOrNotBlankValidator.class)
@Documented
public @interface NullOrNotBlank {
    String message() default "must not be blank";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
