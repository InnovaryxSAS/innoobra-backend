package com.lambdas.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.lambdas.dto.response.ProjectResponseDTO;
import com.lambdas.exception.DatabaseException;
import com.lambdas.mapper.DTOMapper;
import com.lambdas.model.Project;
import com.lambdas.service.ProjectService;
import com.lambdas.service.impl.ProjectServiceImpl;
import com.lambdas.util.HttpStatus;
import com.lambdas.util.LoggingHelper;
import com.lambdas.util.ResponseUtil;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.UUID;

public class GetProjectByIdHandler
        implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger logger = LoggingHelper.getLogger(GetProjectByIdHandler.class);

    private final ProjectService projectService;

    public GetProjectByIdHandler() {
        this.projectService = new ProjectServiceImpl();
    }

    // Constructor para inyección de dependencias (útil para testing)
    public GetProjectByIdHandler(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String requestId = context.getAwsRequestId();
        LoggingHelper.initializeRequestContext(requestId);

        try {
            if (input.getPathParameters() == null) {
                return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Project ID is required");
            }

            String projectIdStr = input.getPathParameters().get("id");
            if (projectIdStr == null || projectIdStr.trim().isEmpty()) {
                return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Project ID is required");
            }

            LoggingHelper.addProjectId(projectIdStr);

            UUID projectId;
            try {
                projectId = UUID.fromString(projectIdStr);
            } catch (IllegalArgumentException e) {
                return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Invalid project ID format");
            }

            Optional<Project> projectOpt = projectService.getProjectById(projectId);

            if (projectOpt.isPresent()) {
                ProjectResponseDTO responseDTO = DTOMapper.toProjectResponseDTO(projectOpt.get());
                return ResponseUtil.createResponse(HttpStatus.OK, responseDTO);
            } else {
                return ResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, "Project not found");
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