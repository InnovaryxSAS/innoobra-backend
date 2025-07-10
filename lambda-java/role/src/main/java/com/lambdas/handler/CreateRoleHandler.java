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
import com.lambdas.service.impl.RoleServiceImpl;
import com.lambdas.service.RoleService;
import com.lambdas.util.HttpStatus;
import com.lambdas.util.LoggingHelper;
import com.lambdas.util.ResponseUtil;
import com.lambdas.util.ValidationHelper;
import com.lambdas.validation.groups.ValidationGroups;
import org.slf4j.Logger;

public class CreateRoleHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger logger = LoggingHelper.getLogger(CreateRoleHandler.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private final RoleService roleService;

    public CreateRoleHandler() {
        this.roleService = new RoleServiceImpl();
    }

    // Constructor para inyección de dependencias (útil para testing)
    public CreateRoleHandler(RoleService roleService) {
        this.roleService = roleService;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String requestId = context.getAwsRequestId();
        LoggingHelper.initializeRequestContext(requestId);

        try {
            LoggingHelper.logProcessStart(logger, "role creation");

            if (input.getBody() == null || input.getBody().trim().isEmpty()) {
                LoggingHelper.logEmptyRequestBody(logger);
                return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Request body is required");
            }

            CreateRoleRequestDTO requestDTO = OBJECT_MAPPER.readValue(input.getBody(), CreateRoleRequestDTO.class);

            ValidationHelper.validateAndThrow(requestDTO, ValidationGroups.Create.class);

            Role role = DTOMapper.toRole(requestDTO);

            Role createdRole = roleService.createRole(role);

            LoggingHelper.logSuccess(logger, "Role creation", createdRole.getIdRole());

            RoleResponseDTO responseDTO = DTOMapper.toRoleResponseDTO(createdRole);

            return ResponseUtil.createResponse(HttpStatus.CREATED, responseDTO);

        } catch (JsonProcessingException e) {
            LoggingHelper.logJsonParsingError(logger, e.getMessage());
            return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Invalid JSON format");
        } catch (ValidationException e) {
            LoggingHelper.logValidationError(logger, e.getMessage());
            return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, e.toMap());
        } catch (RoleAlreadyExistsException e) {
            LoggingHelper.logEntityAlreadyExists(logger, "Role", e.getMessage());
            return ResponseUtil.createErrorResponse(HttpStatus.CONFLICT, e.getMessage());
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