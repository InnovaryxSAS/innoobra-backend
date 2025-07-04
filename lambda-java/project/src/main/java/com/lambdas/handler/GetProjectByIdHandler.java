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

import java.util.Optional;

public class GetProjectByIdHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    
    private static final ProjectService PROJECT_SERVICE = new ProjectService();
    
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String requestId = context.getAwsRequestId();
        context.getLogger().log("Processing request: " + requestId);
        
        try {
            ConnectionPoolManager poolManager = ConnectionPoolManager.getInstance();
            context.getLogger().log("Connection pool status: " + poolManager.getPoolStats());
            context.getLogger().log("Connection pool healthy: " + poolManager.isHealthy());
        } catch (Exception e) {
            context.getLogger().log("Warning: Could not get pool stats: " + e.getMessage());
        }
        
        try {
            String projectId = input.getPathParameters().get("id");
            if (projectId == null || projectId.trim().isEmpty()) {
                return ResponseUtil.createErrorResponse(400, "Project ID is required");
            }
            
            context.getLogger().log("Retrieving project by ID: " + projectId);
            
            Optional<Project> projectOpt = PROJECT_SERVICE.getProjectById(projectId);
            
            if (projectOpt.isPresent()) {
                ProjectResponseDTO responseDTO = DTOMapper.toProjectResponseDTO(projectOpt.get());
                
                context.getLogger().log("Project retrieved successfully: " + projectId);
                
                try {
                    ConnectionPoolManager poolManager = ConnectionPoolManager.getInstance();
                    context.getLogger().log("Final connection pool status: " + poolManager.getPoolStats());
                } catch (Exception e) {
                    context.getLogger().log("Warning: Could not get final pool stats: " + e.getMessage());
                }
                
                return ResponseUtil.createResponse(200, responseDTO);
            } else {
                context.getLogger().log("Project not found: " + projectId);
                return ResponseUtil.createErrorResponse(404, "Project not found");
            }
            
        } catch (DatabaseException e) {
            context.getLogger().log("Database error for request " + requestId + ": " + e.getMessage());
            try {
                ConnectionPoolManager poolManager = ConnectionPoolManager.getInstance();
                context.getLogger().log("Connection pool status on error: " + poolManager.getPoolStats());
                context.getLogger().log("Connection pool healthy on error: " + poolManager.isHealthy());
            } catch (Exception poolException) {
                context.getLogger().log("Could not get pool stats on error: " + poolException.getMessage());
            }
            return ResponseUtil.createErrorResponse(500, "Internal server error");
        } catch (Exception e) {
            context.getLogger().log("Unexpected error for request " + requestId + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseUtil.createErrorResponse(500, "Internal server error");
        }
    }
}