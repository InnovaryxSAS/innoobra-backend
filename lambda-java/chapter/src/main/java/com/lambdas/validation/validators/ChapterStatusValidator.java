package com.lambdas.validation.validators;

import com.lambdas.validation.annotations.ChapterStatusValid;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Set;

public class ChapterStatusValidator implements ConstraintValidator<ChapterStatusValid, String> {
    private static final Set<String> VALID_STATUSES = Set.of(
        "active", "inactive"
    );

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return true;
        }
        return VALID_STATUSES.contains(value.toLowerCase());
    }
}
