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

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class GetUserByCompanyIdHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger logger = LoggingHelper.getLogger(GetUserByCompanyIdHandler.class);

    private final UserService userService;

    public GetUserByCompanyIdHandler() {
        this.userService = new UserServiceImpl();
    }

    // Constructor para inyección de dependencias (útil para testing)
    public GetUserByCompanyIdHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String requestId = context.getAwsRequestId();
        LoggingHelper.initializeRequestContext(requestId);

        try {
            if (input.getPathParameters() == null) {
                return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Company ID is required");
            }

            String companyIdStr = input.getPathParameters().get("companyId");
            if (companyIdStr == null || companyIdStr.trim().isEmpty()) {
                return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Company ID is required");
            }

            LoggingHelper.addCompanyId(companyIdStr);

            UUID companyId;
            try {
                companyId = UUID.fromString(companyIdStr);
            } catch (IllegalArgumentException e) {
                return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Invalid company ID format");
            }

            List<User> users = userService.getUsersByCompanyId(companyId);

            List<UserResponseDTO> responseDTOs = users.stream()
                    .map(DTOMapper::toResponseDTO)
                    .collect(Collectors.toList());

            logger.info("Found {} users for company ID: {}", users.size(), companyId);

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