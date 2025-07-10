package com.lambdas.exception;

import jakarta.validation.ConstraintViolation;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class ValidationException extends RuntimeException {

    @JsonProperty("validationErrors")
    private final Set<String> validationErrors;

    @JsonProperty("fieldErrors")
    private final Map<String, List<String>> fieldErrors;

    @JsonProperty("errorCode")
    private final String errorCode;

    @JsonProperty("timestamp")
    private final long timestamp;

    public ValidationException(String message) {
        super(message);
        this.validationErrors = Set.of(message);
        this.fieldErrors = new HashMap<>();
        this.errorCode = "VALIDATION_ERROR";
        this.timestamp = System.currentTimeMillis();
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
        this.validationErrors = Set.of(message);
        this.fieldErrors = new HashMap<>();
        this.errorCode = "VALIDATION_ERROR";
        this.timestamp = System.currentTimeMillis();
    }

    public ValidationException(Set<String> validationErrors) {
        super("Validation failed: " + String.join(", ", validationErrors));
        this.validationErrors = validationErrors;
        this.fieldErrors = new HashMap<>();
        this.errorCode = "VALIDATION_ERROR";
        this.timestamp = System.currentTimeMillis();
    }

    public ValidationException(String message, String errorCode) {
        super(message);
        this.validationErrors = Set.of(message);
        this.fieldErrors = new HashMap<>();
        this.errorCode = errorCode;
        this.timestamp = System.currentTimeMillis();
    }

    public ValidationException(Set<String> validationErrors, String errorCode) {
        super("Validation failed: " + String.join(", ", validationErrors));
        this.validationErrors = validationErrors;
        this.fieldErrors = new HashMap<>();
        this.errorCode = errorCode;
        this.timestamp = System.currentTimeMillis();
    }

    public <T> ValidationException(Set<ConstraintViolation<T>> violations, boolean isConstraintViolation) {
        super("Validation failed: " + violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", ")));

        this.validationErrors = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());

        this.fieldErrors = violations.stream()
                .collect(Collectors.groupingBy(
                        violation -> violation.getPropertyPath().toString(),
                        Collectors.mapping(
                                ConstraintViolation::getMessage,
                                Collectors.toList())));

        this.errorCode = "BEAN_VALIDATION_ERROR";
        this.timestamp = System.currentTimeMillis();
    }

    public <T> ValidationException(Set<ConstraintViolation<T>> violations, String errorCode,
            boolean isConstraintViolation) {
        super("Validation failed: " + violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", ")));

        this.validationErrors = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());

        this.fieldErrors = violations.stream()
                .collect(Collectors.groupingBy(
                        violation -> violation.getPropertyPath().toString(),
                        Collectors.mapping(
                                ConstraintViolation::getMessage,
                                Collectors.toList())));

        this.errorCode = errorCode;
        this.timestamp = System.currentTimeMillis();
    }

    public ValidationException(Map<String, List<String>> fieldErrors) {
        super("Validation failed for fields: " + fieldErrors.keySet());
        this.fieldErrors = new HashMap<>(fieldErrors);
        this.validationErrors = fieldErrors.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toSet());
        this.errorCode = "FIELD_VALIDATION_ERROR";
        this.timestamp = System.currentTimeMillis();
    }

    public ValidationException(String message, Set<String> validationErrors,
            Map<String, List<String>> fieldErrors, String errorCode) {
        super(message);
        this.validationErrors = validationErrors;
        this.fieldErrors = fieldErrors;
        this.errorCode = errorCode;
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> ValidationException fromConstraintViolations(Set<ConstraintViolation<T>> violations) {
        return new ValidationException(violations, true);
    }

    public static <T> ValidationException fromConstraintViolations(Set<ConstraintViolation<T>> violations,
            String errorCode) {
        return new ValidationException(violations, errorCode, true);
    }

    // Getters
    public Set<String> getValidationErrors() {
        return validationErrors;
    }

    public Map<String, List<String>> getFieldErrors() {
        return fieldErrors;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean hasValidationErrors() {
        return !validationErrors.isEmpty();
    }

    public boolean hasFieldErrors() {
        return !fieldErrors.isEmpty();
    }

    public String getErrorsAsString() {
        return String.join(", ", validationErrors);
    }

    public List<String> getFieldErrors(String fieldName) {
        return fieldErrors.getOrDefault(fieldName, new ArrayList<>());
    }

    public boolean hasFieldError(String fieldName) {
        return fieldErrors.containsKey(fieldName) && !fieldErrors.get(fieldName).isEmpty();
    }

    public String getFirstFieldError(String fieldName) {
        List<String> errors = fieldErrors.get(fieldName);
        return errors != null && !errors.isEmpty() ? errors.get(0) : null;
    }

    public Set<String> getFieldsWithErrors() {
        return fieldErrors.keySet();
    }

    public int getTotalErrorCount() {
        return validationErrors.size();
    }

    public int getTotalFieldErrorCount() {
        return fieldErrors.values().stream()
                .mapToInt(List::size)
                .sum();
    }

    public String getDetailedErrorSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Validation Error Summary:\n");
        summary.append("Error Code: ").append(errorCode).append("\n");
        summary.append("Timestamp: ").append(timestamp).append("\n");
        summary.append("Total Errors: ").append(getTotalErrorCount()).append("\n");

        if (hasFieldErrors()) {
            summary.append("Field Errors:\n");
            fieldErrors.forEach((field, errors) -> {
                summary.append("  ").append(field).append(": ").append(errors).append("\n");
            });
        }

        if (hasValidationErrors()) {
            summary.append("General Errors: ").append(getErrorsAsString()).append("\n");
        }

        return summary.toString();
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("message", getMessage());
        result.put("errorCode", errorCode);
        result.put("timestamp", timestamp);
        result.put("validationErrors", validationErrors);
        result.put("fieldErrors", fieldErrors);
        result.put("totalErrorCount", getTotalErrorCount());
        result.put("fieldsWithErrors", getFieldsWithErrors());
        return result;
    }

    @Override
    public String toString() {
        return "ValidationException{" +
                "message='" + getMessage() + '\'' +
                ", errorCode='" + errorCode + '\'' +
                ", timestamp=" + timestamp +
                ", validationErrors=" + validationErrors +
                ", fieldErrors=" + fieldErrors +
                '}';
    }

    public static ValidationException forCompany(String message) {
        return new ValidationException(message, "COMPANY_VALIDATION_ERROR");
    }

    public static ValidationException forCompanyCreation(Set<String> errors) {
        return new ValidationException(errors, "COMPANY_CREATION_ERROR");
    }

    public static ValidationException forCompanyUpdate(Set<String> errors) {
        return new ValidationException(errors, "COMPANY_UPDATE_ERROR");
    }

    public static ValidationException forProject(String message) {
        return new ValidationException(message, "PROJECT_VALIDATION_ERROR");
    }

    public static ValidationException forProjectCreation(Set<String> errors) {
        return new ValidationException(errors, "PROJECT_CREATION_ERROR");
    }

    public static ValidationException forProjectUpdate(Set<String> errors) {
        return new ValidationException(errors, "PROJECT_UPDATE_ERROR");
    }

    public static ValidationException forRole(String message) {
        return new ValidationException(message, "ROLE_VALIDATION_ERROR");
    }

    public static ValidationException forRoleCreation(Set<String> errors) {
        return new ValidationException(errors, "ROLE_CREATION_ERROR");
    }

    public static ValidationException forRoleUpdate(Set<String> errors) {
        return new ValidationException(errors, "ROLE_UPDATE_ERROR");
    }

    public static ValidationException forUser(String message) {
        return new ValidationException(message, "USER_VALIDATION_ERROR");
    }

    public static ValidationException forUserCreation(Set<String> errors) {
        return new ValidationException(errors, "USER_CREATION_ERROR");
    }

    public static ValidationException forUserUpdate(Set<String> errors) {
        return new ValidationException(errors, "USER_UPDATE_ERROR");
    }

    public static ValidationException forChapter(String message) {
        return new ValidationException(message, "CHAPTER_VALIDATION_ERROR");
    }

    public static ValidationException forChapterCreation(Set<String> errors) {
        return new ValidationException(errors, "CHAPTER_CREATION_ERROR");
    }

    public static ValidationException forChapterUpdate(Set<String> errors) {
        return new ValidationException(errors, "CHAPTER_UPDATE_ERROR");

    public static ValidationException forActivity(String message) {
        return new ValidationException(message, "ACTIVITY_VALIDATION_ERROR");
    }

    public static ValidationException forActivityCreation(Set<String> errors) {
        return new ValidationException(errors, "ACTIVITY_CREATION_ERROR");
    }

    public static ValidationException forActivityUpdate(Set<String> errors) {
        return new ValidationException(errors, "ACTIVITY_UPDATE_ERROR");
    }

    public static ValidationException forRequiredFields(Set<String> missingFields) {
        Set<String> errors = missingFields.stream()
                .map(field -> "Required field '" + field + "' is missing or empty")
                .collect(Collectors.toSet());
        return new ValidationException(errors, "REQUIRED_FIELDS_ERROR");
    }

    public static ValidationException forInvalidFormat(String fieldName, String expectedFormat) {
        String message = "Invalid format for field '" + fieldName + "'. Expected format: " + expectedFormat;
        return new ValidationException(message, "INVALID_FORMAT_ERROR");
    }

    public static ValidationException forDuplicateValue(String fieldName, String value) {
        String message = "Duplicate value '" + value + "' for field '" + fieldName + "'";
        return new ValidationException(message, "DUPLICATE_VALUE_ERROR");
    }

    public static ValidationException forInvalidStatus(String status) {
        String message = "Invalid status '" + status + "'. Valid statuses are: active, inactive, pending, suspended";
        return new ValidationException(message, "INVALID_STATUS_ERROR");
    }
}