package ru.practicum.ewm.main.validation.constraints;

import ru.practicum.ewm.main.validation.validators.DateOfPatternValidator;
import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(ElementType.FIELD)
@Retention(RUNTIME)
@Constraint(validatedBy = DateOfPatternValidator.class)
@Documented
public @interface DateOfPattern {
    String pattern();

    String message() default "incorrect data pattern";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
