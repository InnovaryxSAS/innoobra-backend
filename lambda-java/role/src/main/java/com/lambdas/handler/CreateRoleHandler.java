package com.lambdas.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lambdas.dto.request.CreateRoleRequestDTO;
import com.lambdas.dto.response.RoleResponseDTO;
import com.lambdas.exception.RoleAlreadyExistsException;
import com.lambdas.exception.DatabaseException;
import com.lambdas.exception.ValidationException;
import com.lambdas.mapper.DTOMapper;
import com.lambdas.model.Role;
import com.lambdas.repository.ConnectionPoolManager;
import com.lambdas.service.RoleService;
import com.lambdas.util.ResponseUtil;
import com.lambdas.util.ValidationUtil;

public class CreateRoleHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

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
            if (input.getBody() == null || input.getBody().trim().isEmpty()) {
                return ResponseUtil.createErrorResponse(400, "Request body is required");
            }
            
            CreateRoleRequestDTO requestDTO = OBJECT_MAPPER.readValue(input.getBody(), CreateRoleRequestDTO.class);
            context.getLogger().log("Parsed request DTO: " + requestDTO.toString());
            
            ValidationUtil.ValidationResult validationResult = ValidationUtil.validateCreateRoleRequest(requestDTO);
            if (!validationResult.isValid()) {
                return ResponseUtil.createErrorResponse(400, validationResult.getErrorsAsString());
            }
            
            Role role = DTOMapper.toRole(requestDTO);
            
            Role createdRole = ROLE_SERVICE.createRole(role);
            
            RoleResponseDTO responseDTO = DTOMapper.toRoleResponseDTO(createdRole);
            
            context.getLogger().log("Role created successfully: " + createdRole.getIdRole());
            
            try {
                ConnectionPoolManager poolManager = ConnectionPoolManager.getInstance();
                context.getLogger().log("Final connection pool status: " + poolManager.getPoolStats());
            } catch (Exception e) {
                context.getLogger().log("Warning: Could not get final pool stats: " + e.getMessage());
            }
            
            return ResponseUtil.createResponse(201, responseDTO);
            
        } catch (JsonProcessingException e) {
            context.getLogger().log("JSON parsing error: " + e.getMessage());
            return ResponseUtil.createErrorResponse(400, "Invalid JSON format");
        } catch (ValidationException e) {
            context.getLogger().log("Validation error: " + e.getMessage());
            return ResponseUtil.createErrorResponse(400, e.getMessage());
        } catch (RoleAlreadyExistsException e) {
            context.getLogger().log("Role already exists: " + e.getMessage());
            return ResponseUtil.createErrorResponse(409, e.getMessage());
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