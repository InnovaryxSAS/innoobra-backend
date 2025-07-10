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
            LoggingHelper.logProcessStart(logger, "user retrieval by ID");

            if (input.getPathParameters() == null) {
                LoggingHelper.logMissingParameter(logger, "Path parameters");
                return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "User ID is required");
            }

            String userId = input.getPathParameters().get("id");
            if (userId == null || userId.trim().isEmpty()) {
                LoggingHelper.logMissingParameter(logger, "User ID");
                return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "User ID is required");
            }

            LoggingHelper.addUserId(userId);

            Optional<User> userOpt = userService.getUserById(userId);

            if (userOpt.isPresent()) {
                LoggingHelper.logSuccess(logger, "User retrieval", userId);

                UserResponseDTO responseDTO = DTOMapper.toResponseDTO(userOpt.get());

                return ResponseUtil.createResponse(HttpStatus.OK, responseDTO);
            } else {
                LoggingHelper.logEntityNotFound(logger, "User", userId);
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