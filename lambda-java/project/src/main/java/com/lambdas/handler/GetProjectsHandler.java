package com.lambdas.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.lambdas.dto.response.ProjectResponseDTO;
import com.lambdas.exception.DatabaseException;
import com.lambdas.mapper.DTOMapper;
import com.lambdas.model.Project;
import com.lambdas.repository.ConnectionPoolManager;
import com.lambdas.service.ProjectService;
import com.lambdas.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.List;

public class GetProjectsHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger logger = LoggerFactory.getLogger(GetProjectsHandler.class);
    private static final ProjectService PROJECT_SERVICE = new ProjectService();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String requestId = context.getAwsRequestId();
        MDC.put("requestId", requestId);

        try {
            logger.info("Starting projects retrieval process");
            logConnectionPoolStatus();

            List<Project> projects = PROJECT_SERVICE.getAllProjects();
            logger.info("Retrieved {} projects successfully", projects.size());

            List<ProjectResponseDTO> responseDTOs = DTOMapper.toProjectResponseDTOList(projects);

            logFinalConnectionPoolStatus();

            return ResponseUtil.createResponse(200, responseDTOs);

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