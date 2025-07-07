package com.lambdas.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.lambdas.dto.response.RoleResponseDTO;
import com.lambdas.exception.DatabaseException;
import com.lambdas.mapper.DTOMapper;
import com.lambdas.model.Role;
import com.lambdas.repository.ConnectionPoolManager;
import com.lambdas.service.RoleService;
import com.lambdas.util.ResponseUtil;

import java.util.Optional;

public class GetRoleByIdHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    
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
            
            context.getLogger().log("Retrieving role by ID: " + roleId);
            
            Optional<Role> roleOpt = ROLE_SERVICE.getRoleById(roleId);
            
            if (roleOpt.isPresent()) {
                RoleResponseDTO responseDTO = DTOMapper.toRoleResponseDTO(roleOpt.get());
                
                context.getLogger().log("Role retrieved successfully: " + roleId);
                
                try {
                    ConnectionPoolManager poolManager = ConnectionPoolManager.getInstance();
                    context.getLogger().log("Final connection pool status: " + poolManager.getPoolStats());
                } catch (Exception e) {
                    context.getLogger().log("Warning: Could not get final pool stats: " + e.getMessage());
                }
                
                return ResponseUtil.createResponse(200, responseDTO);
            } else {
                context.getLogger().log("Role not found: " + roleId);
                return ResponseUtil.createErrorResponse(404, "Role not found");
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