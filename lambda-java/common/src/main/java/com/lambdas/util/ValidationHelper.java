package com.lambdas.util;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.groups.Default;
import com.lambdas.exception.ValidationException;

import java.util.Set;

public class ValidationHelper {

    private static final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private static final Validator validator = validatorFactory.getValidator();

    public static <T> Set<ConstraintViolation<T>> validate(T object, Class<?>... groups) {
        Class<?>[] validationGroups = groups.length > 0 ? groups : new Class<?>[]{Default.class};
        return validator.validate(object, validationGroups);
    }

    public static <T> boolean isValid(T object, Class<?>... groups) {
        return validate(object, groups).isEmpty();
    }

    public static <T> void validateAndThrow(T object, Class<?>... groups) {
        Set<ConstraintViolation<T>> violations = validate(object, groups);
        if (!violations.isEmpty()) {
            throw ValidationException.fromConstraintViolations(violations);
        }
    }

    public static <T> ValidationResult<T> validateAndGetResult(T object, Class<?>... groups) {
        Set<ConstraintViolation<T>> violations = validate(object, groups);
        return new ValidationResult<>(violations);
    }

    public static void validateAllAndThrow(Object... objects) {
        for (Object obj : objects) {
            if (obj != null) {
                validateAndThrow(obj);
            }
        }
    }

    public static <T> Set<ConstraintViolation<T>> validateProperty(T object, String propertyName, Class<?>... groups) {
        Class<?>[] validationGroups = groups.length > 0 ? groups : new Class<?>[]{Default.class};
        return validator.validateProperty(object, propertyName, validationGroups);
    }

    public static <T> Set<ConstraintViolation<T>> validateValue(Class<T> beanType, String propertyName, 
                                                               Object value, Class<?>... groups) {
        Class<?>[] validationGroups = groups.length > 0 ? groups : new Class<?>[]{Default.class};
        return validator.validateValue(beanType, propertyName, value, validationGroups);
    }

    public static void close() {
        validatorFactory.close();
    }

    public static class ValidationResult<T> {
        private final Set<ConstraintViolation<T>> violations;

        public ValidationResult(Set<ConstraintViolation<T>> violations) {
            this.violations = violations;
        }

        public boolean isValid() {
            return violations.isEmpty();
        }

        public Set<ConstraintViolation<T>> getViolations() {
            return violations;
        }

        public ValidationException toException() {
            return ValidationException.fromConstraintViolations(violations);
        }

        public void throwIfInvalid() {
            if (!isValid()) {
                throw toException();
            }
        }

        public String getErrorsAsString() {
            return violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");
        }
    }
}