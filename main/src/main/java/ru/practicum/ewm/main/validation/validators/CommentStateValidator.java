package ru.practicum.ewm.main.validation.validators;

import ru.practicum.ewm.main.validation.constraints.CommentState;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CommentStateValidator implements ConstraintValidator<CommentState, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }

        try {
            ru.practicum.ewm.main.comment.model.CommentState.valueOf(value);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }
}
