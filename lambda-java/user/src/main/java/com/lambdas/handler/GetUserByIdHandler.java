package com.lambdas.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.lambdas.dto.response.UserResponseDTO;
import com.lambdas.exception.DatabaseException;
import com.lambdas.mapper.DTOMapper;
import com.lambdas.model.User;
import com.lambdas.service.UserService;
import com.lambdas.service.impl.UserServiceImpl;
import com.lambdas.util.HttpStatus;
import com.lambdas.util.LoggingHelper;
import com.lambdas.util.ResponseUtil;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.UUID;

public class GetUserByIdHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger logger = LoggingHelper.getLogger(GetUserByIdHandler.class);

    private final UserService userService;

    public GetUserByIdHandler() {
        this.userService = new UserServiceImpl();
    }

    // Constructor para inyección de dependencias (útil para testing)
    public GetUserByIdHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String requestId = context.getAwsRequestId();
        LoggingHelper.initializeRequestContext(requestId);

        try {
            if (input.getPathParameters() == null) {
                return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "User ID is required");
            }

            String userIdStr = input.getPathParameters().get("id");
            if (userIdStr == null || userIdStr.trim().isEmpty()) {
                return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "User ID is required");
            }

            LoggingHelper.addUserId(userIdStr);

            UUID userId;
            try {
                userId = UUID.fromString(userIdStr);
            } catch (IllegalArgumentException e) {
                return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Invalid user ID format");
            }

            Optional<User> userOpt = userService.getUserById(userId);

            if (userOpt.isPresent()) {
                UserResponseDTO responseDTO = DTOMapper.toResponseDTO(userOpt.get());
                return ResponseUtil.createResponse(HttpStatus.OK, responseDTO);
            } else {
                return ResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, "User not found");
            }

        } catch (DatabaseException e) {
            LoggingHelper.logDatabaseError(logger, e.getMessage(), e);
            return ResponseUtil.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        } catch (Exception e) {
            LoggingHelper.logUnexpectedError(logger, e.getMessage(), e);
            return ResponseUtil.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        } finally {
            LoggingHelper.clearContext();
        }
    }
}