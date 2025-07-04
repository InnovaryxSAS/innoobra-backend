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

public class DeleteProjectHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    
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
            
            context.getLogger().log("Attempting to delete project: " + projectId);
            
            boolean deleted = PROJECT_SERVICE.deleteProject(projectId);
            
            if (deleted) {
                DeleteProjectResponseDTO responseDTO = new DeleteProjectResponseDTO.Builder()
                        .message("Project successfully deactivated")
                        .projectId(projectId)
                        .success(true)
                        .build();
                
                context.getLogger().log("Project deleted successfully: " + projectId);
                
                try {
                    ConnectionPoolManager poolManager = ConnectionPoolManager.getInstance();
                    context.getLogger().log("Final connection pool status: " + poolManager.getPoolStats());
                } catch (Exception e) {
                    context.getLogger().log("Warning: Could not get final pool stats: " + e.getMessage());
                }
                
                return ResponseUtil.createResponse(200, responseDTO);
            } else {
                DeleteProjectResponseDTO responseDTO = new DeleteProjectResponseDTO.Builder()
                        .message("Project not found")
                        .projectId(projectId)
                        .success(false)
                        .build();
                
                context.getLogger().log("Project not found for deletion: " + projectId);
                return ResponseUtil.createResponse(404, responseDTO);
            }
            
        } catch (ProjectNotFoundException e) {
            context.getLogger().log("Project not found: " + e.getMessage());
            
            DeleteProjectResponseDTO responseDTO = new DeleteProjectResponseDTO.Builder()
                    .message(e.getMessage())
                    .projectId(input.getPathParameters().get("id"))
                    .success(false)
                    .build();
                    
            return ResponseUtil.createResponse(404, responseDTO);
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