package com.lambdas.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.lambdas.dto.response.ActivityResponseDTO;
import com.lambdas.exception.DatabaseException;
import com.lambdas.mapper.DTOMapper;
import com.lambdas.model.Activity;
import com.lambdas.service.ActivityService;
import com.lambdas.service.impl.ActivityServiceImpl;
import com.lambdas.util.HttpStatus;
import com.lambdas.util.LoggingHelper;
import com.lambdas.util.ResponseUtil;
import org.slf4j.Logger;

import java.util.List;

public class GetActivitiesHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    
    private static final Logger logger = LoggingHelper.getLogger(GetActivitiesHandler.class);

    private final ActivityService activityService;

    public GetActivitiesHandler() {
        this.activityService = new ActivityServiceImpl();
    }

    // Constructor para inyección de dependencias (útil para testing)
    public GetActivitiesHandler(ActivityService activityService) {
        this.activityService = activityService;
    }
    
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String requestId = context.getAwsRequestId();
        LoggingHelper.initializeRequestContext(requestId);
        
        try {
            LoggingHelper.logProcessStart(logger, "activities retrieval");
            
            List<Activity> activities = activityService.getAllActivities();
            LoggingHelper.logSuccessWithCount(logger, "Activities retrieval", activities.size());
            
            List<ActivityResponseDTO> responseDTOs = DTOMapper.toResponseDTOList(activities);
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