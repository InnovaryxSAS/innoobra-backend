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
import com.lambdas.util.ValidationHelper;
import com.lambdas.validation.groups.ValidationGroups;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Optional;

public class UpdateRoleHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    
    private static final Logger logger = LoggerFactory.getLogger(UpdateRoleHandler.class);
    private static final RoleService ROLE_SERVICE = new RoleService();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());
    
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String requestId = context.getAwsRequestId();
        
        MDC.put("requestId", requestId);
        
        try {
            String roleId = input.getPathParameters().get("id");
            
            MDC.put("roleId", roleId);
            
            logger.info("Starting role update process");
            logConnectionPoolStatus();
            
            if (roleId == null || roleId.trim().isEmpty()) {
                logger.warn("Role ID is missing or empty");
                return ResponseUtil.createErrorResponse(400, "Role ID is required");
            }
            
            if (input.getBody() == null || input.getBody().trim().isEmpty()) {
                logger.warn("Request body is empty or null");
                return ResponseUtil.createErrorResponse(400, "Request body is required");
            }
            
            UpdateRoleRequestDTO requestDTO = OBJECT_MAPPER.readValue(input.getBody(), UpdateRoleRequestDTO.class);
            
            ValidationHelper.validateAndThrow(requestDTO, ValidationGroups.Update.class);
            
            Optional<Role> existingRoleOpt = ROLE_SERVICE.getRoleById(roleId);
            if (!existingRoleOpt.isPresent()) {
                logger.warn("Role not found for update");
                return ResponseUtil.createErrorResponse(404, "Role not found");
            }
            
            Role existingRole = existingRoleOpt.get();
            
            Role updatedRole = DTOMapper.updateRoleFromDTO(existingRole, requestDTO);
            
            Role savedRole = ROLE_SERVICE.updateRole(updatedRole);
            logger.info("Role updated successfully");
            
            RoleResponseDTO responseDTO = DTOMapper.toRoleResponseDTO(savedRole);
            
            logFinalConnectionPoolStatus();
            
            return ResponseUtil.createResponse(200, responseDTO);
            
        } catch (JsonProcessingException e) {
            logger.error("JSON parsing error: {}", e.getMessage());
            return ResponseUtil.createErrorResponse(400, "Invalid JSON format");
        } catch (ValidationException e) {
            logger.warn("Validation error: {}", e.getMessage());
            return ResponseUtil.createErrorResponse(400, e.toMap());
        } catch (RoleNotFoundException e) {
            logger.warn("Role not found: {}", e.getMessage());
            return ResponseUtil.createErrorResponse(404, e.getMessage());
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
                        poolManager.getPoolStats(), poolManager.isHealthy());
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