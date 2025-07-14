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
import com.lambdas.service.impl.ProjectServiceImpl;
import com.lambdas.util.HttpStatus;
import com.lambdas.util.LoggingHelper;
import com.lambdas.util.ResponseUtil;
import org.slf4j.Logger;

import java.util.List;

public class GetProjectHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger logger = LoggingHelper.getLogger(GetProjectHandler.class);

    private final ProjectService projectService;

    public GetProjectHandler() {
        this.projectService = new ProjectServiceImpl();
    }

    // Constructor para inyección de dependencias (útil para testing)
    public GetProjectHandler(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String requestId = context.getAwsRequestId();
        LoggingHelper.initializeRequestContext(requestId);

        try {
            LoggingHelper.logProcessStart(logger, "projects retrieval");
            logConnectionPoolStatus();

            List<Project> projects = projectService.getAllProjects();
            LoggingHelper.logSuccessWithCount(logger, "Projects retrieval", projects.size());

            List<ProjectResponseDTO> responseDTOs = DTOMapper.toProjectResponseDTOList(projects);

            logFinalConnectionPoolStatus();

            return ResponseUtil.createResponse(HttpStatus.OK, responseDTOs);

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

    private void logConnectionPoolStatus() {
        try {
            ConnectionPoolManager poolManager = ConnectionPoolManager.getInstance();
            poolManager.getPoolStats();
            poolManager.isHealthy();
        } catch (Exception e) {
            LoggingHelper.logConnectionPoolWarning(logger,
                    "Could not retrieve connection pool status: " + e.getMessage());
        }
    }

    private void logFinalConnectionPoolStatus() {
        try {
            ConnectionPoolManager poolManager = ConnectionPoolManager.getInstance();
        } catch (Exception e) {
            LoggingHelper.logConnectionPoolWarning(logger,
                    "Could not retrieve final connection pool status: " + e.getMessage());
        }
    }

    private void logConnectionPoolStatusOnError() {
        try {
            ConnectionPoolManager poolManager = ConnectionPoolManager.getInstance();
            LoggingHelper.logConnectionPoolError(logger, poolManager.getPoolStats().toString(),
                    poolManager.isHealthy());
        } catch (Exception e) {
            LoggingHelper.logConnectionPoolWarning(logger,
                    "Could not retrieve connection pool status on error: " + e.getMessage());
        }
    }
}