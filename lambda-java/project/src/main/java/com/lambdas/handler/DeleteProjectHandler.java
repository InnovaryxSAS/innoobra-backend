package com.lambdas.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.lambdas.dto.response.DeleteResponseDTO;
import com.lambdas.exception.ProjectNotFoundException;
import com.lambdas.exception.DatabaseException;
import com.lambdas.service.ProjectService;
import com.lambdas.service.impl.ProjectServiceImpl;
import com.lambdas.util.HttpStatus;
import com.lambdas.util.LoggingHelper;
import com.lambdas.util.ResponseUtil;
import org.slf4j.Logger;

import java.util.UUID;

public class DeleteProjectHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger logger = LoggingHelper.getLogger(DeleteProjectHandler.class);

    private final ProjectService projectService;

    public DeleteProjectHandler() {
        this.projectService = new ProjectServiceImpl();
    }

    // Constructor para inyección de dependencias (útil para testing)
    public DeleteProjectHandler(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String requestId = context.getAwsRequestId();
        LoggingHelper.initializeRequestContext(requestId);

        try {
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

            boolean deleted = projectService.deleteProject(projectId);

            if (deleted) {
                DeleteResponseDTO responseDTO = new DeleteResponseDTO.Builder()
                        .message("Project successfully deactivated")
                        .projectId(projectIdStr)
                        .success(true)
                        .build();

                return ResponseUtil.createResponse(HttpStatus.OK, responseDTO);
            } else {
                DeleteResponseDTO responseDTO = new DeleteResponseDTO.Builder()
                        .message("Project not found")
                        .projectId(projectIdStr)
                        .success(false)
                        .build();

                return ResponseUtil.createResponse(HttpStatus.NOT_FOUND, responseDTO);
            }

        } catch (ProjectNotFoundException e) {
            LoggingHelper.logEntityNotFound(logger, "Project", e.getMessage());

            DeleteResponseDTO responseDTO = new DeleteResponseDTO.Builder()
                    .message(e.getMessage())
                    .projectId(input.getPathParameters().get("id"))
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