package com.lambdas.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.lambdas.dto.response.DeleteResponseDTO;
import com.lambdas.exception.UserNotFoundException;
import com.lambdas.exception.DatabaseException;
import com.lambdas.service.UserService;
import com.lambdas.service.impl.UserServiceImpl;
import com.lambdas.util.HttpStatus;
import com.lambdas.util.LoggingHelper;
import com.lambdas.util.ResponseUtil;
import org.slf4j.Logger;

public class DeleteUserHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    
    private static final Logger logger = LoggingHelper.getLogger(DeleteUserHandler.class);

    private final UserService userService;

    public DeleteUserHandler() {
        this.userService = new UserServiceImpl();
    }

    // Constructor para inyección de dependencias (útil para testing)
    public DeleteUserHandler(UserService userService) {
        this.userService = userService;
    }
    
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String requestId = context.getAwsRequestId();
        LoggingHelper.initializeRequestContext(requestId);
        
        try {
            LoggingHelper.logProcessStart(logger, "user deletion");
            
            String userId = input.getPathParameters().get("id");
            if (userId == null || userId.trim().isEmpty()) {
                LoggingHelper.logMissingParameter(logger, "User ID");
                return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "User ID is required");
            }
            
            LoggingHelper.addUserId(userId);
            
            boolean deleted = userService.deactivateUser(userId);
            
            if (deleted) {
                LoggingHelper.logSuccess(logger, "User deletion", userId);
                
                DeleteResponseDTO responseDTO = new DeleteResponseDTO.Builder()
                        .message("User successfully deactivated")
                        .userId(userId)
                        .success(true)
                        .build();
                
                return ResponseUtil.createResponse(HttpStatus.OK, responseDTO);
            } else {
                LoggingHelper.logEntityNotFound(logger, "User", userId);
                
                DeleteResponseDTO responseDTO = new DeleteResponseDTO.Builder()
                        .message("User not found")
                        .userId(userId)
                        .success(false)
                        .build();
                
                return ResponseUtil.createResponse(HttpStatus.NOT_FOUND, responseDTO);
            }
            
        } catch (UserNotFoundException e) {
            LoggingHelper.logEntityNotFound(logger, "User", e.getMessage());
            
            DeleteResponseDTO responseDTO = new DeleteResponseDTO.Builder()
                    .message(e.getMessage())
                    .userId(input.getPathParameters().get("id"))
                    .success(false)
                    .build();
                    
            return ResponseUtil.createResponse(HttpStatus.NOT_FOUND, responseDTO);
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