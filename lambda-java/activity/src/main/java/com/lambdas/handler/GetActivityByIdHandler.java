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

import java.util.Optional;

public class GetActivityByIdHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger logger = LoggingHelper.getLogger(GetActivityByIdHandler.class);

    private final ActivityService activityService;

    public GetActivityByIdHandler() {
        this.activityService = new ActivityServiceImpl();
    }

    // Constructor para inyección de dependencias (útil para testing)
    public GetActivityByIdHandler(ActivityService activityService) {
        this.activityService = activityService;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String requestId = context.getAwsRequestId();
        LoggingHelper.initializeRequestContext(requestId);

        try {
            LoggingHelper.logProcessStart(logger, "activity retrieval by ID");

            if (input.getPathParameters() == null) {
                LoggingHelper.logMissingParameter(logger, "Path parameters");
                return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Activity ID is required");
            }

            String activityId = input.getPathParameters().get("id");
            if (activityId == null || activityId.trim().isEmpty()) {
                LoggingHelper.logMissingParameter(logger, "Activity ID");
                return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Activity ID is required");
            }

            LoggingHelper.addUserId(activityId); // Reutilizamos el método existente

            Optional<Activity> activityOpt = activityService.getActivityById(activityId);

            if (activityOpt.isPresent()) {
                LoggingHelper.logSuccess(logger, "Activity retrieval", activityId);

                ActivityResponseDTO responseDTO = DTOMapper.toResponseDTO(activityOpt.get());

                return ResponseUtil.createResponse(HttpStatus.OK, responseDTO);
            } else {
                LoggingHelper.logEntityNotFound(logger, "Activity", activityId);
                return ResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, "Activity not found");
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