package com.lambdas.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.lambdas.dto.response.DeleteProjectResponseDTO;
import com.lambdas.exception.ProjectNotFoundException;
import com.lambdas.exception.DatabaseException;
import com.lambdas.repository.ConnectionPoolManager;
import com.lambdas.service.ProjectService;
import com.lambdas.service.impl.ProjectServiceImpl;
import com.lambdas.util.HttpStatus;
import com.lambdas.util.LoggingHelper;
import com.lambdas.util.ResponseUtil;
import org.slf4j.Logger;

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
            String projectId = input.getPathParameters().get("id");
            if (projectId == null || projectId.trim().isEmpty()) {
                LoggingHelper.logMissingParameter(logger, "Project ID");
                return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Project ID is required");
            }

            LoggingHelper.logProcessStart(logger, "project deletion");

            boolean deleted = projectService.deleteProject(projectId);

            if (deleted) {
                LoggingHelper.logSuccess(logger, "Project deletion", projectId);

                DeleteProjectResponseDTO responseDTO = new DeleteProjectResponseDTO.Builder()
                        .message("Project successfully deactivated")
                        .projectId(projectId)
                        .success(true)
                        .build();

                return ResponseUtil.createResponse(HttpStatus.OK, responseDTO);
            } else {
                LoggingHelper.logEntityNotFound(logger, "Project", projectId);

                DeleteProjectResponseDTO responseDTO = new DeleteProjectResponseDTO.Builder()
                        .message("Project not found")
                        .projectId(projectId)
                        .success(false)
                        .build();

                return ResponseUtil.createResponse(HttpStatus.NOT_FOUND, responseDTO);
            }

        } catch (ProjectNotFoundException e) {
            LoggingHelper.logEntityNotFound(logger, "Project", e.getMessage());

            DeleteProjectResponseDTO responseDTO = new DeleteProjectResponseDTO.Builder()
                    .message(e.getMessage())
                    .projectId(input.getPathParameters().get("id"))
                    .success(false)
                    .build();

            return ResponseUtil.createResponse(HttpStatus.NOT_FOUND, responseDTO);
        } catch (DatabaseException e) {
            LoggingHelper.logDatabaseError(logger, e.getMessage(), e);
            logConnectionPoolStatusOnError();
            return ResponseUtil.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        } catch (Exception e) {
            LoggingHelper.logUnexpectedError(logger, e.getMessage(), e);
            return ResponseUtil.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        } finally {
            LoggingHelper.clearContext();
        }
    }

    private void logConnectionPoolStatusOnError() {
        try {
            ConnectionPoolManager poolManager = ConnectionPoolManager.getInstance();
            if (!poolManager.isHealthy()) {
                LoggingHelper.logConnectionPoolError(logger, 
                    poolManager.getPoolStats().toString(), false);
            }
        } catch (Exception e) {
            LoggingHelper.logConnectionPoolWarning(logger, 
                "Connection pool health check failed: " + e.getMessage());
        }
    }
}