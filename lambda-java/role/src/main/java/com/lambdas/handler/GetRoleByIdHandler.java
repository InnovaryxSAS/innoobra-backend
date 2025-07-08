package com.lambdas.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.lambdas.dto.response.RoleResponseDTO;
import com.lambdas.exception.DatabaseException;
import com.lambdas.mapper.DTOMapper;
import com.lambdas.model.Role;
import com.lambdas.service.RoleService;
import com.lambdas.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Optional;

public class GetRoleByIdHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    
    private static final Logger logger = LoggerFactory.getLogger(GetRoleByIdHandler.class);
    private static final RoleService ROLE_SERVICE = new RoleService();
    
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String requestId = context.getAwsRequestId();
        MDC.put("requestId", requestId);
        
        try {
            logger.info("Starting role retrieval by ID process");
            
            String roleId = input.getPathParameters().get("id");
            if (roleId == null || roleId.trim().isEmpty()) {
                logger.warn("Role ID is missing or empty");
                return ResponseUtil.createErrorResponse(400, "Role ID is required");
            }
            
            MDC.put("roleId", roleId);
            logger.debug("Processing retrieval for role ID: {}", roleId);
            
            Optional<Role> roleOpt = ROLE_SERVICE.getRoleById(roleId);
            
            if (roleOpt.isPresent()) {
                logger.info("Role retrieved successfully with ID: {}", roleId);
                
                RoleResponseDTO responseDTO = DTOMapper.toRoleResponseDTO(roleOpt.get());
                logger.debug("Mapped Role entity to response DTO");
                
                return ResponseUtil.createResponse(200, responseDTO);
            } else {
                logger.warn("Role not found with ID: {}", roleId);
                return ResponseUtil.createErrorResponse(404, "Role not found");
            }
            
        } catch (DatabaseException e) {
            logger.error("Database error occurred", e);
            return ResponseUtil.createErrorResponse(500, "Internal server error");
        } catch (Exception e) {
            logger.error("Unexpected error occurred", e);
            return ResponseUtil.createErrorResponse(500, "Internal server error");
        } finally {
            MDC.clear();
        }
    }
}