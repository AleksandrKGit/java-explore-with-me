package ru.practicum.ewm.main.validation.validators;

import ru.practicum.ewm.main.validation.constraints.NullOrNotBlank;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NullOrNotBlankValidator implements ConstraintValidator<NullOrNotBlank, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        return value == null || !value.isBlank();
    }
}
