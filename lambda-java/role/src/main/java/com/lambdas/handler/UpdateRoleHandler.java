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
import com.lambdas.service.impl.RoleServiceImpl;
import com.lambdas.service.RoleService;
import com.lambdas.util.HttpStatus;
import com.lambdas.util.LoggingHelper;
import com.lambdas.util.ResponseUtil;
import com.lambdas.util.ValidationHelper;
import com.lambdas.validation.groups.ValidationGroups;
import org.slf4j.Logger;

import java.util.Optional;

public class UpdateRoleHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger logger = LoggingHelper.getLogger(UpdateRoleHandler.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private final RoleService roleService;

    public UpdateRoleHandler() {
        this.roleService = new RoleServiceImpl();
    }

    // Constructor para inyección de dependencias (útil para testing)
    public UpdateRoleHandler(RoleService roleService) {
        this.roleService = roleService;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String requestId = context.getAwsRequestId();
        LoggingHelper.initializeRequestContext(requestId);

        try {
            String roleId = input.getPathParameters().get("id");
            if (roleId == null || roleId.trim().isEmpty()) {
                LoggingHelper.logMissingParameter(logger, "Role ID");
                return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Role ID is required");
            }

            LoggingHelper.addUserId(roleId); // Agregamos el roleId al contexto de logging
            LoggingHelper.logProcessStart(logger, "role update");
            logConnectionPoolStatus();

            if (input.getBody() == null || input.getBody().trim().isEmpty()) {
                LoggingHelper.logEmptyRequestBody(logger);
                return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Request body is required");
            }

            UpdateRoleRequestDTO requestDTO = OBJECT_MAPPER.readValue(input.getBody(), UpdateRoleRequestDTO.class);

            ValidationHelper.validateAndThrow(requestDTO, ValidationGroups.Update.class);

            Optional<Role> existingRoleOpt = roleService.getRoleById(roleId);
            if (!existingRoleOpt.isPresent()) {
                LoggingHelper.logEntityNotFound(logger, "Role", roleId);
                return ResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, "Role not found");
            }

            Role existingRole = existingRoleOpt.get();

            Role updatedRole = DTOMapper.updateRoleFromDTO(existingRole, requestDTO);

            Role savedRole = roleService.updateRole(updatedRole);

            LoggingHelper.logSuccess(logger, "Role update", roleId);

            RoleResponseDTO responseDTO = DTOMapper.toRoleResponseDTO(savedRole);

            logFinalConnectionPoolStatus();

            return ResponseUtil.createResponse(HttpStatus.OK, responseDTO);

        } catch (JsonProcessingException e) {
            LoggingHelper.logJsonParsingError(logger, e.getMessage());
            return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Invalid JSON format");
        } catch (ValidationException e) {
            LoggingHelper.logValidationError(logger, e.getMessage());
            return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, e.toMap());
        } catch (RoleNotFoundException e) {
            LoggingHelper.logEntityNotFound(logger, "Role", e.getMessage());
            return ResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (DatabaseException e) {
            LoggingHelper.logDatabaseError(logger, e.getMessage(), e);
            logConnectionPoolStatusOnError();
            return ResponseUtil.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        } catch (Exception e) {
            LoggingHelper.logUnexpectedError(logger, e.getMessage(), e);
            return ResponseUtil.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        } finally {
            LoggingHelper.clearContext();
        }
    }

    private void logConnectionPoolStatus() {
        try {
            ConnectionPoolManager poolManager = ConnectionPoolManager.getInstance();
            poolManager.getPoolStats();
            poolManager.isHealthy();
        } catch (Exception e) {
            LoggingHelper.logConnectionPoolWarning(logger,
                    "Could not retrieve connection pool status: " + e.getMessage());
        }
    }

    private void logFinalConnectionPoolStatus() {
        try {
            ConnectionPoolManager poolManager = ConnectionPoolManager.getInstance();
        } catch (Exception e) {
            LoggingHelper.logConnectionPoolWarning(logger,
                    "Could not retrieve final connection pool status: " + e.getMessage());
        }
    }

    private void logConnectionPoolStatusOnError() {
        try {
            ConnectionPoolManager poolManager = ConnectionPoolManager.getInstance();
            LoggingHelper.logConnectionPoolError(logger, poolManager.getPoolStats().toString(),
                    poolManager.isHealthy());
        } catch (Exception e) {
            LoggingHelper.logConnectionPoolWarning(logger,
                    "Could not retrieve connection pool status on error: " + e.getMessage());
        }
    }
}