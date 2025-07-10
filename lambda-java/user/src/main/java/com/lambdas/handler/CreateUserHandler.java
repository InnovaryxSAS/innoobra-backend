package com.lambdas.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lambdas.dto.request.CreateUserRequestDTO;
import com.lambdas.dto.response.UserResponseDTO;
import com.lambdas.exception.UserAlreadyExistsException;
import com.lambdas.exception.DatabaseException;
import com.lambdas.exception.ValidationException;
import com.lambdas.mapper.DTOMapper;
import com.lambdas.model.User;
import com.lambdas.service.UserService;
import com.lambdas.service.impl.UserServiceImpl;
import com.lambdas.util.HttpStatus;
import com.lambdas.util.LoggingHelper;
import com.lambdas.util.ResponseUtil;
import com.lambdas.util.ValidationHelper;
import com.lambdas.validation.groups.ValidationGroups;
import org.slf4j.Logger;

public class CreateUserHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger logger = LoggingHelper.getLogger(CreateUserHandler.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());
    
    private final UserService userService;

    public CreateUserHandler() {
        this.userService = new UserServiceImpl();
    }

    // Constructor para inyección de dependencias (útil para testing)
    public CreateUserHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String requestId = context.getAwsRequestId();
        LoggingHelper.initializeRequestContext(requestId);
        
        try {
            LoggingHelper.logProcessStart(logger, "user creation");
            
            if (input.getBody() == null || input.getBody().trim().isEmpty()) {
                LoggingHelper.logEmptyRequestBody(logger);
                return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Request body is required");
            }
            
            CreateUserRequestDTO requestDTO = OBJECT_MAPPER.readValue(input.getBody(), CreateUserRequestDTO.class);
            
            ValidationHelper.validateAndThrow(requestDTO, ValidationGroups.Create.class);
            
            User user = DTOMapper.toUser(requestDTO);
            
            User createdUser = userService.createUser(user);
            
            UserResponseDTO responseDTO = DTOMapper.toResponseDTO(createdUser);
            
            LoggingHelper.logSuccess(logger, "User creation", createdUser.getIdUser());
            
            return ResponseUtil.createResponse(HttpStatus.CREATED, responseDTO);
            
        } catch (JsonProcessingException e) {
            LoggingHelper.logJsonParsingError(logger, e.getMessage());
            return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Invalid JSON format");
        } catch (ValidationException e) {
            LoggingHelper.logValidationError(logger, e.getMessage());
            return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, e.toMap());
        } catch (UserAlreadyExistsException e) {
            LoggingHelper.logEntityAlreadyExists(logger, "User", e.getMessage());
            return ResponseUtil.createErrorResponse(HttpStatus.CONFLICT, e.getMessage());
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