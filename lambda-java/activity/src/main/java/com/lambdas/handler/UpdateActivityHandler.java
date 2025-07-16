package com.lambdas.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lambdas.dto.request.UpdateActivityRequestDTO;
import com.lambdas.dto.response.ActivityResponseDTO;
import com.lambdas.exception.ActivityNotFoundException;
import com.lambdas.exception.DatabaseException;
import com.lambdas.exception.ValidationException;
import com.lambdas.mapper.DTOMapper;
import com.lambdas.model.Activity;
import com.lambdas.repository.ConnectionPoolManager;
import com.lambdas.service.ActivityService;
import com.lambdas.service.impl.ActivityServiceImpl;
import com.lambdas.util.HttpStatus;
import com.lambdas.util.LoggingHelper;
import com.lambdas.util.ResponseUtil;
import com.lambdas.util.ValidationHelper;
import com.lambdas.validation.groups.ValidationGroups;
import org.slf4j.Logger;

import java.util.Optional;

public class UpdateActivityHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger logger = LoggingHelper.getLogger(UpdateActivityHandler.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private final ActivityService activityService;

    public UpdateActivityHandler() {
        this.activityService = new ActivityServiceImpl();
    }

    // Constructor para inyección de dependencias (útil para testing)
    public UpdateActivityHandler(ActivityService activityService) {
        this.activityService = activityService;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String requestId = context.getAwsRequestId();
        LoggingHelper.initializeRequestContext(requestId);

        try {
            String activityId = null;
            if (input.getPathParameters() != null) {
                activityId = input.getPathParameters().get("id");
            }
            
            if (activityId == null || activityId.trim().isEmpty()) {
                LoggingHelper.logMissingParameter(logger, "Activity ID");
                return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Activity ID is required");
            }

            LoggingHelper.addUserId(activityId);
            LoggingHelper.logProcessStart(logger, "activity update");

            if (input.getBody() == null || input.getBody().trim().isEmpty()) {
                LoggingHelper.logEmptyRequestBody(logger);
                return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Request body is required");
            }

            UpdateActivityRequestDTO requestDTO = OBJECT_MAPPER.readValue(input.getBody(), UpdateActivityRequestDTO.class);

            ValidationHelper.validateAndThrow(requestDTO, ValidationGroups.Update.class);

            Optional<Activity> existingActivityOpt = activityService.getActivityById(activityId);
            if (!existingActivityOpt.isPresent()) {
                LoggingHelper.logEntityNotFound(logger, "Activity", activityId);
                return ResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, "Activity not found");
            }

            Activity existingActivity = existingActivityOpt.get();

            Activity updatedActivity = DTOMapper.updateActivityFromDTO(existingActivity, requestDTO);
            Activity savedActivity = activityService.updateActivity(updatedActivity);

            LoggingHelper.logSuccess(logger, "Activity update", activityId);

            ActivityResponseDTO responseDTO = DTOMapper.toResponseDTO(savedActivity);

            return ResponseUtil.createResponse(HttpStatus.OK, responseDTO);

        } catch (JsonProcessingException e) {
            LoggingHelper.logJsonParsingError(logger, e.getMessage());
            return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Invalid JSON format");
        } catch (ValidationException e) {
            LoggingHelper.logValidationError(logger, e.getMessage());
            return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, e.toMap());
        } catch (ActivityNotFoundException e) {
            LoggingHelper.logEntityNotFound(logger, "Activity", e.getMessage());
            return ResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (DatabaseException e) {
            LoggingHelper.logDatabaseError(logger, e.getMessage(), e);
            logConnectionPoolStatusOnError();
            return ResponseUtil.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        } catch (Exception e) {
            LoggingHelper.logUnexpectedError(logger, e.getMessage(), e);
            return ResponseUtil.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        } finally {
            LoggingHelper.clearContext();
        }
    }

    private void logConnectionPoolStatusOnError() {
        try {
            ConnectionPoolManager poolManager = ConnectionPoolManager.getInstance();
            if (!poolManager.isHealthy()) {
                LoggingHelper.logConnectionPoolError(logger, 
                    poolManager.getPoolStats().toString(), false);
            }
        } catch (Exception e) {
            LoggingHelper.logConnectionPoolWarning(logger, 
                "Connection pool health check failed: " + e.getMessage());
        }
    }
}