package com.lambdas.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lambdas.dto.request.CreateActivityRequestDTO;
import com.lambdas.dto.response.ActivityResponseDTO;
import com.lambdas.exception.ActivityAlreadyExistsException;
import com.lambdas.exception.DatabaseException;
import com.lambdas.exception.ValidationException;
import com.lambdas.mapper.DTOMapper;
import com.lambdas.model.Activity;
import com.lambdas.service.ActivityService;
import com.lambdas.service.impl.ActivityServiceImpl;
import com.lambdas.util.HttpStatus;
import com.lambdas.util.LoggingHelper;
import com.lambdas.util.ResponseUtil;
import com.lambdas.util.ValidationHelper;
import com.lambdas.validation.groups.ValidationGroups;
import org.slf4j.Logger;

public class CreateActivityHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger logger = LoggingHelper.getLogger(CreateActivityHandler.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());
    
    private final ActivityService activityService;

    public CreateActivityHandler() {
        this.activityService = new ActivityServiceImpl();
    }

    // Constructor para inyección de dependencias (útil para testing)
    public CreateActivityHandler(ActivityService activityService) {
        this.activityService = activityService;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String requestId = context.getAwsRequestId();
        LoggingHelper.initializeRequestContext(requestId);
        
        try {
            LoggingHelper.logProcessStart(logger, "activity creation");
            
            if (input.getBody() == null || input.getBody().trim().isEmpty()) {
                LoggingHelper.logEmptyRequestBody(logger);
                return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Request body is required");
            }
            
            CreateActivityRequestDTO requestDTO = OBJECT_MAPPER.readValue(input.getBody(), CreateActivityRequestDTO.class);
            
            ValidationHelper.validateAndThrow(requestDTO, ValidationGroups.Create.class);
            
            Activity activity = DTOMapper.toActivity(requestDTO);
            
            Activity createdActivity = activityService.createActivity(activity);
            
            ActivityResponseDTO responseDTO = DTOMapper.toResponseDTO(createdActivity);
            
            LoggingHelper.logSuccess(logger, "Activity creation", createdActivity.getIdActivity());
            
            return ResponseUtil.createResponse(HttpStatus.CREATED, responseDTO);
            
        } catch (JsonProcessingException e) {
            LoggingHelper.logJsonParsingError(logger, e.getMessage());
            return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Invalid JSON format");
        } catch (ValidationException e) {
            LoggingHelper.logValidationError(logger, e.getMessage());
            return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, e.toMap());
        } catch (ActivityAlreadyExistsException e) {
            LoggingHelper.logEntityAlreadyExists(logger, "Activity", e.getMessage());
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