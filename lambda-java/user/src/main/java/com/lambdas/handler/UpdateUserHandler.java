package com.lambdas.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lambdas.dto.request.UpdateUserRequestDTO;
import com.lambdas.dto.response.UserResponseDTO;
import com.lambdas.exception.UserNotFoundException;
import com.lambdas.exception.DatabaseException;
import com.lambdas.exception.ValidationException;
import com.lambdas.mapper.DTOMapper;
import com.lambdas.model.User;
import com.lambdas.repository.ConnectionPoolManager;
import com.lambdas.service.UserService;
import com.lambdas.service.impl.UserServiceImpl;
import com.lambdas.util.HttpStatus;
import com.lambdas.util.LoggingHelper;
import com.lambdas.util.ResponseUtil;
import com.lambdas.util.ValidationHelper;
import com.lambdas.validation.groups.ValidationGroups;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.UUID;

public class UpdateUserHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger logger = LoggingHelper.getLogger(UpdateUserHandler.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private final UserService userService;

    public UpdateUserHandler() {
        this.userService = new UserServiceImpl();
    }

    // Constructor para inyección de dependencias (útil para testing)
    public UpdateUserHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String requestId = context.getAwsRequestId();
        LoggingHelper.initializeRequestContext(requestId);

        try {
            String userIdStr = null;
            if (input.getPathParameters() != null) {
                userIdStr = input.getPathParameters().get("id");
            }
            
            if (userIdStr == null || userIdStr.trim().isEmpty()) {
                return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "User ID is required");
            }

            LoggingHelper.addUserId(userIdStr);

            UUID userId;
            try {
                userId = UUID.fromString(userIdStr);
            } catch (IllegalArgumentException e) {
                LoggingHelper.logValidationError(logger, "Invalid UUID format: " + userIdStr);
                return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Invalid user ID format");
            }

            if (input.getBody() == null || input.getBody().trim().isEmpty()) {
                return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Request body is required");
            }

            UpdateUserRequestDTO requestDTO = OBJECT_MAPPER.readValue(input.getBody(), 
                    UpdateUserRequestDTO.class);

            ValidationHelper.validateAndThrow(requestDTO, ValidationGroups.Update.class);

            Optional<User> existingUserOpt = userService.getUserById(userId);
            if (!existingUserOpt.isPresent()) {
                LoggingHelper.logEntityNotFound(logger, "User", userIdStr);
                return ResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, "User not found");
            }

            User existingUser = existingUserOpt.get();
            User updatedUser = DTOMapper.updateUserFromDTO(existingUser, requestDTO);
            User savedUser = userService.updateUser(updatedUser);

            UserResponseDTO responseDTO = DTOMapper.toResponseDTO(savedUser);
            return ResponseUtil.createResponse(HttpStatus.OK, responseDTO);

        } catch (JsonProcessingException e) {
            LoggingHelper.logJsonParsingError(logger, e.getMessage());
            return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Invalid JSON format");
        } catch (ValidationException e) {
            LoggingHelper.logValidationError(logger, e.getMessage());
            return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, e.toMap());
        } catch (UserNotFoundException e) {
            LoggingHelper.logEntityNotFound(logger, "User", e.getMessage());
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