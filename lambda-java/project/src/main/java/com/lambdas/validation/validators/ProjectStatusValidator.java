package com.lambdas.validation.validators;

import com.lambdas.validation.annotations.ProjectStatusValid;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Set;

public class ProjectStatusValidator implements ConstraintValidator<ProjectStatusValid, String> {
    private static final Set<String> VALID_STATUSES = Set.of(
        "active", "inactive", "pending", "suspended", "cancelled", "completed"
    );

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return true;
        }
        return VALID_STATUSES.contains(value.toLowerCase());
    }
}
