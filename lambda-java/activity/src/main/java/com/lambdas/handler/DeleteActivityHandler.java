package com.lambdas.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.lambdas.dto.response.DeleteResponseDTO;
import com.lambdas.exception.ActivityNotFoundException;
import com.lambdas.exception.DatabaseException;
import com.lambdas.service.ActivityService;
import com.lambdas.service.impl.ActivityServiceImpl;
import com.lambdas.util.HttpStatus;
import com.lambdas.util.LoggingHelper;
import com.lambdas.util.ResponseUtil;
import org.slf4j.Logger;

public class DeleteActivityHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    
    private static final Logger logger = LoggingHelper.getLogger(DeleteActivityHandler.class);

    private final ActivityService activityService;

    public DeleteActivityHandler() {
        this.activityService = new ActivityServiceImpl();
    }

    // Constructor para inyección de dependencias (útil para testing)
    public DeleteActivityHandler(ActivityService activityService) {
        this.activityService = activityService;
    }
    
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String requestId = context.getAwsRequestId();
        LoggingHelper.initializeRequestContext(requestId);
        
        try {
            LoggingHelper.logProcessStart(logger, "activity deletion");
            
            String activityId = input.getPathParameters().get("id");
            if (activityId == null || activityId.trim().isEmpty()) {
                LoggingHelper.logMissingParameter(logger, "Activity ID");
                return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Activity ID is required");
            }
            
            LoggingHelper.addUserId(activityId); // Reutilizamos el método existente
            
            boolean deleted = activityService.deactivateActivity(activityId);
            
            if (deleted) {
                LoggingHelper.logSuccess(logger, "Activity deletion", activityId);
                
                DeleteResponseDTO responseDTO = new DeleteResponseDTO.Builder()
                        .message("Activity successfully deactivated")
                        .activityId(activityId)
                        .success(true)
                        .build();
                
                return ResponseUtil.createResponse(HttpStatus.OK, responseDTO);
            } else {
                LoggingHelper.logEntityNotFound(logger, "Activity", activityId);
                
                DeleteResponseDTO responseDTO = new DeleteResponseDTO.Builder()
                        .message("Activity not found")
                        .activityId(activityId)
                        .success(false)
                        .build();
                
                return ResponseUtil.createResponse(HttpStatus.NOT_FOUND, responseDTO);
            }
            
        } catch (ActivityNotFoundException e) {
            LoggingHelper.logEntityNotFound(logger, "Activity", e.getMessage());
            
            DeleteResponseDTO responseDTO = new DeleteResponseDTO.Builder()
                    .message(e.getMessage())
                    .activityId(input.getPathParameters().get("id"))
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