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
            LoggingHelper.logProcessStart(logger, "role deletion");

            String roleId = input.getPathParameters().get("id");
            if (roleId == null || roleId.trim().isEmpty()) {
                LoggingHelper.logMissingParameter(logger, "Role ID");
                return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Role ID is required");
            }

            LoggingHelper.addUserId(roleId); // Agregamos el roleId al contexto de logging

            boolean deleted = roleService.deactivateRole(roleId);

            if (deleted) {
                LoggingHelper.logSuccess(logger, "Role deletion", roleId);

                DeleteRoleResponseDTO responseDTO = new DeleteRoleResponseDTO.Builder()
                        .message("Role successfully deleted")
                        .roleId(roleId)
                        .success(true)
                        .build();

                return ResponseUtil.createResponse(HttpStatus.OK, responseDTO);
            } else {
                LoggingHelper.logEntityNotFound(logger, "Role", roleId);

                DeleteRoleResponseDTO responseDTO = new DeleteRoleResponseDTO.Builder()
                        .message("Role not found")
                        .roleId(roleId)
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