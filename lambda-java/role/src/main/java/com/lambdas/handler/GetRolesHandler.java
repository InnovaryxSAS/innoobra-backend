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

import java.util.List;

public class GetRolesHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    
    private static final Logger logger = LoggerFactory.getLogger(GetRolesHandler.class);
    private static final RoleService ROLE_SERVICE = new RoleService();
    
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String requestId = context.getAwsRequestId();
        MDC.put("requestId", requestId);
        
        try {
            logger.info("Starting roles retrieval process");
            
            List<Role> roles = ROLE_SERVICE.getAllRoles();
            logger.info("Retrieved {} roles successfully", roles.size());
            
            List<RoleResponseDTO> responseDTOs = DTOMapper.toRoleResponseDTOList(roles);
            
            return ResponseUtil.createResponse(200, responseDTOs);
            
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