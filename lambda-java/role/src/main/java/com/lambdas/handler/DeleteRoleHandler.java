package com.lambdas.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.lambdas.dto.response.DeleteRoleResponseDTO;
import com.lambdas.exception.RoleNotFoundException;
import com.lambdas.exception.DatabaseException;
import com.lambdas.repository.ConnectionPoolManager;
import com.lambdas.service.RoleService;
import com.lambdas.util.ResponseUtil;

public class DeleteRoleHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    
    private static final RoleService ROLE_SERVICE = new RoleService();
    
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
            String roleId = input.getPathParameters().get("id");
            if (roleId == null || roleId.trim().isEmpty()) {
                return ResponseUtil.createErrorResponse(400, "Role ID is required");
            }
            
            context.getLogger().log("Attempting to delete role (logical): " + roleId);
            
            boolean deleted = ROLE_SERVICE.deactivateRole(roleId);
            
            if (deleted) {
                DeleteRoleResponseDTO responseDTO = new DeleteRoleResponseDTO.Builder()
                        .message("Role successfully deleted")
                        .roleId(roleId)
                        .success(true)
                        .build();
                
                context.getLogger().log("Role deleted successfully (logical): " + roleId);
                
                try {
                    ConnectionPoolManager poolManager = ConnectionPoolManager.getInstance();
                    context.getLogger().log("Final connection pool status: " + poolManager.getPoolStats());
                } catch (Exception e) {
                    context.getLogger().log("Warning: Could not get final pool stats: " + e.getMessage());
                }
                
                return ResponseUtil.createResponse(200, responseDTO);
            } else {
                DeleteRoleResponseDTO responseDTO = new DeleteRoleResponseDTO.Builder()
                        .message("Role not found")
                        .roleId(roleId)
                        .success(false)
                        .build();
                
                context.getLogger().log("Role not found for deletion: " + roleId);
                return ResponseUtil.createResponse(404, responseDTO);
            }
            
        } catch (RoleNotFoundException e) {
            context.getLogger().log("Role not found: " + e.getMessage());
            
            DeleteRoleResponseDTO responseDTO = new DeleteRoleResponseDTO.Builder()
                    .message(e.getMessage())
                    .roleId(input.getPathParameters().get("id"))
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