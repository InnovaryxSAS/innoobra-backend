package com.lambdas.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.lambdas.dto.response.DeleteRoleResponseDTO;
import com.lambdas.exception.RoleNotFoundException;
import com.lambdas.exception.DatabaseException;
import com.lambdas.service.impl.RoleServiceImpl;
import com.lambdas.service.RoleService;
import com.lambdas.util.HttpStatus;
import com.lambdas.util.LoggingHelper;
import com.lambdas.util.ResponseUtil;
import org.slf4j.Logger;

import java.util.UUID;

public class DeleteRoleHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger logger = LoggingHelper.getLogger(DeleteRoleHandler.class);

    private final RoleService roleService;

    public DeleteRoleHandler() {
        this.roleService = new RoleServiceImpl();
    }

    // Constructor para inyección de dependencias (útil para testing)
    public DeleteRoleHandler(RoleService roleService) {
        this.roleService = roleService;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String requestId = context.getAwsRequestId();
        LoggingHelper.initializeRequestContext(requestId);

        try {
            String roleIdStr = input.getPathParameters().get("id");
            if (roleIdStr == null || roleIdStr.trim().isEmpty()) {
                return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Role ID is required");
            }
            
            LoggingHelper.addUserId(roleIdStr);

            UUID roleId;
            try {
                roleId = UUID.fromString(roleIdStr);
            } catch (IllegalArgumentException e) {
                return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Invalid role ID format");
            }

            boolean deleted = roleService.deactivateRole(roleId);

            if (deleted) {
                DeleteRoleResponseDTO responseDTO = new DeleteRoleResponseDTO.Builder()
                        .message("Role successfully deleted")
                        .roleId(roleIdStr)
                        .success(true)
                        .build();

                return ResponseUtil.createResponse(HttpStatus.OK, responseDTO);
            } else {
                DeleteRoleResponseDTO responseDTO = new DeleteRoleResponseDTO.Builder()
                        .message("Role not found")
                        .roleId(roleIdStr)
                        .success(false)
                        .build();

                return ResponseUtil.createResponse(HttpStatus.NOT_FOUND, responseDTO);
            }

        } catch (RoleNotFoundException e) {
            LoggingHelper.logEntityNotFound(logger, "Role", e.getMessage());

            DeleteRoleResponseDTO responseDTO = new DeleteRoleResponseDTO.Builder()
                    .message(e.getMessage())
                    .roleId(input.getPathParameters().get("id"))
                    .success(false)
                    .build();

            return ResponseUtil.createResponse(HttpStatus.NOT_FOUND, responseDTO);
        } catch (DatabaseException e) {
            LoggingHelper.logDatabaseError(logger, e.getMessage(), e);
            return ResponseUtil.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        } catch (Exception e) {
            LoggingHelper.logUnexpectedError(logger, e.getMessage(), e);
            return ResponseUtil.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        } finally {
            LoggingHelper.clearContext();
        }
    }
}