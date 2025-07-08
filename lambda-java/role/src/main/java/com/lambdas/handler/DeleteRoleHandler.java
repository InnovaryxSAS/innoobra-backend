package com.lambdas.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.lambdas.dto.response.DeleteRoleResponseDTO;
import com.lambdas.exception.RoleNotFoundException;
import com.lambdas.exception.DatabaseException;
import com.lambdas.service.RoleService;
import com.lambdas.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class DeleteRoleHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    
    private static final Logger logger = LoggerFactory.getLogger(DeleteRoleHandler.class);
    private static final RoleService ROLE_SERVICE = new RoleService();
    
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String requestId = context.getAwsRequestId();
        MDC.put("requestId", requestId);
        
        try {
            logger.info("Starting role deletion process");
            
            String roleId = input.getPathParameters().get("id");
            if (roleId == null || roleId.trim().isEmpty()) {
                logger.warn("Role ID is missing or empty");
                return ResponseUtil.createErrorResponse(400, "Role ID is required");
            }
            
            MDC.put("roleId", roleId);
            
            boolean deleted = ROLE_SERVICE.deactivateRole(roleId);
            
            if (deleted) {
                logger.info("Role deleted successfully with ID: {}", roleId);
                
                DeleteRoleResponseDTO responseDTO = new DeleteRoleResponseDTO.Builder()
                        .message("Role successfully deleted")
                        .roleId(roleId)
                        .success(true)
                        .build();
                
                return ResponseUtil.createResponse(200, responseDTO);
            } else {
                logger.warn("Role not found for deletion with ID: {}", roleId);
                
                DeleteRoleResponseDTO responseDTO = new DeleteRoleResponseDTO.Builder()
                        .message("Role not found")
                        .roleId(roleId)
                        .success(false)
                        .build();
                
                return ResponseUtil.createResponse(404, responseDTO);
            }
            
        } catch (RoleNotFoundException e) {
            logger.warn("Role not found: {}", e.getMessage());
            
            DeleteRoleResponseDTO responseDTO = new DeleteRoleResponseDTO.Builder()
                    .message(e.getMessage())
                    .roleId(input.getPathParameters().get("id"))
                    .success(false)
                    .build();
                    
            return ResponseUtil.createResponse(404, responseDTO);
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