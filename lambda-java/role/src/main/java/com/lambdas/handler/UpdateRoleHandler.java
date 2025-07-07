package com.lambdas.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lambdas.dto.request.UpdateRoleRequestDTO;
import com.lambdas.dto.response.RoleResponseDTO;
import com.lambdas.exception.RoleNotFoundException;
import com.lambdas.exception.DatabaseException;
import com.lambdas.exception.ValidationException;
import com.lambdas.mapper.DTOMapper;
import com.lambdas.model.Role;
import com.lambdas.repository.ConnectionPoolManager;
import com.lambdas.service.RoleService;
import com.lambdas.util.ResponseUtil;
import com.lambdas.util.ValidationUtil;

import java.util.Optional;

public class UpdateRoleHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    
    private static final RoleService ROLE_SERVICE = new RoleService();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());
    
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
            
            if (input.getBody() == null || input.getBody().trim().isEmpty()) {
                return ResponseUtil.createErrorResponse(400, "Request body is required");
            }
            
            UpdateRoleRequestDTO requestDTO = OBJECT_MAPPER.readValue(input.getBody(), UpdateRoleRequestDTO.class);
            context.getLogger().log("Parsed update request DTO for role: " + roleId);
            
            ValidationUtil.ValidationResult validationResult = ValidationUtil.validateUpdateRoleRequest(requestDTO);
            if (!validationResult.isValid()) {
                return ResponseUtil.createErrorResponse(400, validationResult.getErrorsAsString());
            }
            
            Optional<Role> existingRoleOpt = ROLE_SERVICE.getRoleById(roleId);
            if (!existingRoleOpt.isPresent()) {
                return ResponseUtil.createErrorResponse(404, "Role not found");
            }
            
            Role existingRole = existingRoleOpt.get();
            
            Role updatedRole = DTOMapper.updateRoleFromDTO(existingRole, requestDTO);
            
            Role savedRole = ROLE_SERVICE.updateRole(updatedRole);
            
            RoleResponseDTO responseDTO = DTOMapper.toRoleResponseDTO(savedRole);
            
            context.getLogger().log("Role updated successfully: " + roleId);
            
            try {
                ConnectionPoolManager poolManager = ConnectionPoolManager.getInstance();
                context.getLogger().log("Final connection pool status: " + poolManager.getPoolStats());
            } catch (Exception e) {
                context.getLogger().log("Warning: Could not get final pool stats: " + e.getMessage());
            }
            
            return ResponseUtil.createResponse(200, responseDTO);
            
        } catch (JsonProcessingException e) {
            context.getLogger().log("JSON parsing error: " + e.getMessage());
            return ResponseUtil.createErrorResponse(400, "Invalid JSON format");
        } catch (ValidationException e) {
            context.getLogger().log("Validation error: " + e.getMessage());
            return ResponseUtil.createErrorResponse(400, e.getMessage());
        } catch (RoleNotFoundException e) {
            context.getLogger().log("Role not found: " + e.getMessage());
            return ResponseUtil.createErrorResponse(404, e.getMessage());
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