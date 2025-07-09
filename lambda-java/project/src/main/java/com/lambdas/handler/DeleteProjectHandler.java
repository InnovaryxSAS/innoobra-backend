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
import com.lambdas.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class DeleteProjectHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger logger = LoggerFactory.getLogger(DeleteProjectHandler.class);
    private static final ProjectService PROJECT_SERVICE = new ProjectService();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String requestId = context.getAwsRequestId();
        MDC.put("requestId", requestId);

        try {
            logger.info("Starting project deletion process");
            logConnectionPoolStatus();

            String projectId = input.getPathParameters().get("id");
            if (projectId == null || projectId.trim().isEmpty()) {
                logger.warn("Project ID is missing or empty");
                return ResponseUtil.createErrorResponse(400, "Project ID is required");
            }

            MDC.put("projectId", projectId);

            boolean deleted = PROJECT_SERVICE.deleteProject(projectId);

            if (deleted) {
                logger.info("Project deleted successfully with ID: {}", projectId);

                DeleteProjectResponseDTO responseDTO = new DeleteProjectResponseDTO.Builder()
                        .message("Project successfully deactivated")
                        .projectId(projectId)
                        .success(true)
                        .build();

                logFinalConnectionPoolStatus();

                return ResponseUtil.createResponse(200, responseDTO);
            } else {
                logger.warn("Project not found for deletion with ID: {}", projectId);

                DeleteProjectResponseDTO responseDTO = new DeleteProjectResponseDTO.Builder()
                        .message("Project not found")
                        .projectId(projectId)
                        .success(false)
                        .build();

                return ResponseUtil.createResponse(404, responseDTO);
            }

        } catch (ProjectNotFoundException e) {
            logger.warn("Project not found: {}", e.getMessage());

            DeleteProjectResponseDTO responseDTO = new DeleteProjectResponseDTO.Builder()
                    .message(e.getMessage())
                    .projectId(input.getPathParameters().get("id"))
                    .success(false)
                    .build();

            return ResponseUtil.createResponse(404, responseDTO);
        } catch (DatabaseException e) {
            logger.error("Database error occurred", e);
            logConnectionPoolStatusOnError();
            return ResponseUtil.createErrorResponse(500, "Internal server error");
        } catch (Exception e) {
            logger.error("Unexpected error occurred", e);
            return ResponseUtil.createErrorResponse(500, "Internal server error");
        } finally {
            MDC.clear();
        }
    }

    private void logConnectionPoolStatus() {
        try {
            ConnectionPoolManager poolManager = ConnectionPoolManager.getInstance();
            poolManager.getPoolStats();
            poolManager.isHealthy();
        } catch (Exception e) {
            logger.warn("Could not retrieve connection pool status: {}", e.getMessage());
        }
    }

    private void logFinalConnectionPoolStatus() {
        try {
            ConnectionPoolManager poolManager = ConnectionPoolManager.getInstance();
        } catch (Exception e) {
            logger.warn("Could not retrieve final connection pool status: {}", e.getMessage());
        }
    }

    private void logConnectionPoolStatusOnError() {
        try {
            ConnectionPoolManager poolManager = ConnectionPoolManager.getInstance();
            logger.error("Connection pool status on error: {}, healthy: {}",
                    poolManager.getPoolStats(), poolManager.isHealthy());
        } catch (Exception e) {
            logger.error("Could not retrieve connection pool status on error: {}", e.getMessage());
        }
    }
}