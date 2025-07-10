package com.lambdas.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.lambdas.dto.response.RoleResponseDTO;
import com.lambdas.exception.DatabaseException;
import com.lambdas.mapper.DTOMapper;
import com.lambdas.model.Role;
import com.lambdas.service.impl.RoleServiceImpl;
import com.lambdas.service.RoleService;
import com.lambdas.util.HttpStatus;
import com.lambdas.util.LoggingHelper;
import com.lambdas.util.ResponseUtil;
import org.slf4j.Logger;

import java.util.Optional;

public class GetRoleByIdHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger logger = LoggingHelper.getLogger(GetRoleByIdHandler.class);

    private final RoleService roleService;

    public GetRoleByIdHandler() {
        this.roleService = new RoleServiceImpl();
    }

    // Constructor para inyección de dependencias (útil para testing)
    public GetRoleByIdHandler(RoleService roleService) {
        this.roleService = roleService;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String requestId = context.getAwsRequestId();
        LoggingHelper.initializeRequestContext(requestId);

        try {
            LoggingHelper.logProcessStart(logger, "role retrieval by ID");

            String roleId = input.getPathParameters().get("id");
            if (roleId == null || roleId.trim().isEmpty()) {
                LoggingHelper.logMissingParameter(logger, "Role ID");
                return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Role ID is required");
            }

            LoggingHelper.addUserId(roleId); // Agregamos el roleId al contexto de logging

            Optional<Role> roleOpt = roleService.getRoleById(roleId);

            if (roleOpt.isPresent()) {
                LoggingHelper.logSuccess(logger, "Role retrieval", roleId);

                RoleResponseDTO responseDTO = DTOMapper.toRoleResponseDTO(roleOpt.get());

                return ResponseUtil.createResponse(HttpStatus.OK, responseDTO);
            } else {
                LoggingHelper.logEntityNotFound(logger, "Role", roleId);
                return ResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, "Role not found");
            }

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