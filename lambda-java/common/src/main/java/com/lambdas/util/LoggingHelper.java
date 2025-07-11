package com.lambdas.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public final class LoggingHelper {
    
    private static final String REQUEST_ID_KEY = "requestId";
    private static final String COMPANY_ID_KEY = "companyId";
    private static final String USER_ID_KEY = "userId";
    private static final String ACTIVITY_ID_KEY = "activityId";
    private static final String ATTRIBUTE_ID_KEY = "attributeId";
    private static final String CHAPTER_ID_KEY = "chapterId";
    private static final String PROJECT_ID_KEY = "projectId";
    private static final String ROLE_ID_KEY = "roleId";
    
    private LoggingHelper() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static void initializeRequestContext(String requestId) {
        MDC.put(REQUEST_ID_KEY, requestId);
    }

    public static void addCompanyId(String companyId) {
        if (companyId != null && !companyId.trim().isEmpty()) {
            MDC.put(COMPANY_ID_KEY, companyId);
        }
    }

    public static void addUserId(String userId) {
        if (userId != null && !userId.trim().isEmpty()) {
            MDC.put(USER_ID_KEY, userId);
        }
    }

    public static void addActivityId(String activityId) {
        if (activityId != null && !activityId.trim().isEmpty()) {
            MDC.put(ACTIVITY_ID_KEY, activityId);
        }
    }

    public static void addAttributeId(String attributeId) {
        if (attributeId != null && !attributeId.trim().isEmpty()) {
            MDC.put(ATTRIBUTE_ID_KEY, attributeId);
        }
    }

    public static void addChapterId(String chapterId) {
        if (chapterId != null && !chapterId.trim().isEmpty()) {
            MDC.put(CHAPTER_ID_KEY, chapterId);
        }
    }

    public static void addProjectId(String projectId) {
        if (projectId != null && !projectId.trim().isEmpty()) {
            MDC.put(PROJECT_ID_KEY, projectId);
        }
    }

    public static void addRoleId(String roleId) {
        if (roleId != null && !roleId.trim().isEmpty()) {
            MDC.put(ROLE_ID_KEY, roleId);
        }
    }

    public static void clearContext() {
        MDC.clear();
    }

    public static void logProcessStart(Logger logger, String processName) {
        logger.info("Starting {} process", processName);
    }

    public static void logSuccess(Logger logger, String operation, String entityId) {
        logger.info("{} completed successfully for entity ID: {}", operation, entityId);
    }

    public static void logSuccessWithCount(Logger logger, String operation, int count) {
        logger.info("{} completed successfully. Count: {}", operation, count);
    }

    public static void logMissingParameter(Logger logger, String parameterName) {
        logger.warn("{} is missing or empty", parameterName);
    }

    public static void logEntityNotFound(Logger logger, String entityType, String entityId) {
        logger.warn("{} not found with ID: {}", entityType, entityId);
    }

    public static void logEntityAlreadyExists(Logger logger, String entityType, String message) {
        logger.warn("{} already exists: {}", entityType, message);
    }

    public static void logJsonParsingError(Logger logger, String errorMessage) {
        logger.error("JSON parsing error: {}", errorMessage);
    }

    public static void logValidationError(Logger logger, String errorMessage) {
        logger.warn("Validation error: {}", errorMessage);
    }

    public static void logDatabaseError(Logger logger, String errorMessage, Exception e) {
        logger.error("Database error occurred: {}", errorMessage, e);
    }

    public static void logUnexpectedError(Logger logger, String errorMessage, Exception e) {
        logger.error("Unexpected error occurred: {}", errorMessage, e);
    }

    public static void logConnectionPoolWarning(Logger logger, String message) {
        logger.warn("Connection pool warning: {}", message);
    }

    public static void logConnectionPoolError(Logger logger, String message, boolean isHealthy) {
        logger.error("Connection pool error: {}, healthy: {}", message, isHealthy);
    }

    public static void logEmptyRequestBody(Logger logger) {
        logger.warn("Request body is empty or null");
    }

    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    public static <T> T executeWithLogging(String requestId, String operation, 
                                         Logger logger, LoggingOperation<T> operation1) throws Exception {
        try {
            initializeRequestContext(requestId);
            logProcessStart(logger, operation);
            return operation1.execute();
        } finally {
            clearContext();
        }
    }

    @FunctionalInterface
    public interface LoggingOperation<T> {
        T execute() throws Exception;
    }
}