package com.lambdas.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.lambdas.dto.response.UserResponseDTO;
import com.lambdas.exception.DatabaseException;
import com.lambdas.mapper.DTOMapper;
import com.lambdas.model.User;
import com.lambdas.model.UserStatus;
import com.lambdas.service.UserService;
import com.lambdas.service.impl.UserServiceImpl;
import com.lambdas.util.HttpStatus;
import com.lambdas.util.LoggingHelper;
import com.lambdas.util.ResponseUtil;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;

public class GetUsersHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    
    private static final Logger logger = LoggingHelper.getLogger(GetUsersHandler.class);

    private final UserService userService;

    public GetUsersHandler() {
        this.userService = new UserServiceImpl();
    }

    // Constructor para inyección de dependencias (útil para testing)
    public GetUsersHandler(UserService userService) {
        this.userService = userService;
    }
    
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String requestId = context.getAwsRequestId();
        LoggingHelper.initializeRequestContext(requestId);
        
        try {
            LoggingHelper.logProcessStart(logger, "users retrieval");
            
            List<User> users = userService.getAllUsers();
            LoggingHelper.logSuccessWithCount(logger, "Users retrieval", users.size());
            
            List<UserResponseDTO> responseDTOs = DTOMapper.toResponseDTOList(users);
            return ResponseUtil.createResponse(HttpStatus.OK, responseDTOs);
            
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