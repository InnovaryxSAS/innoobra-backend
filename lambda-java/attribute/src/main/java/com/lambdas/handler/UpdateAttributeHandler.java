package com.lambdas.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lambdas.dto.request.UpdateAttributeRequestDTO;
import com.lambdas.dto.response.AttributeResponseDTO;
import com.lambdas.exception.AttributeNotFoundException;
import com.lambdas.exception.DatabaseException;
import com.lambdas.exception.ValidationException;
import com.lambdas.mapper.DTOMapper;
import com.lambdas.model.Attribute;
import com.lambdas.repository.ConnectionPoolManager;
import com.lambdas.service.AttributeService;
import com.lambdas.service.impl.AttributeServiceImpl;
import com.lambdas.util.HttpStatus;
import com.lambdas.util.LoggingHelper;
import com.lambdas.util.ResponseUtil;
import com.lambdas.util.ValidationHelper;
import com.lambdas.validation.groups.ValidationGroups;
import org.slf4j.Logger;

import java.util.Optional;

public class UpdateAttributeHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger logger = LoggingHelper.getLogger(UpdateAttributeHandler.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private final AttributeService attributeService;

    public UpdateAttributeHandler() {
        this.attributeService = new AttributeServiceImpl();
    }

    // Constructor para inyección de dependencias (útil para testing)
    public UpdateAttributeHandler(AttributeService attributeService) {
        this.attributeService = attributeService;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String requestId = context.getAwsRequestId();
        LoggingHelper.initializeRequestContext(requestId);

        try {
            String attributeId = null;
            if (input.getPathParameters() != null) {
                attributeId = input.getPathParameters().get("id");
            }
            
            if (attributeId == null || attributeId.trim().isEmpty()) {
                return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Attribute ID is required");
            }

            LoggingHelper.addAttributeId(attributeId);

            if (input.getBody() == null || input.getBody().trim().isEmpty()) {
                return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Request body is required");
            }

            UpdateAttributeRequestDTO requestDTO = OBJECT_MAPPER.readValue(input.getBody(), UpdateAttributeRequestDTO.class);

            ValidationHelper.validateAndThrow(requestDTO, ValidationGroups.Update.class);

            Optional<Attribute> existingAttributeOpt = attributeService.getAttributeById(attributeId);
            if (!existingAttributeOpt.isPresent()) {
                LoggingHelper.logEntityNotFound(logger, "Attribute", attributeId);
                return ResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, "Attribute not found");
            }

            Attribute existingAttribute = existingAttributeOpt.get();
            Attribute updatedAttribute = DTOMapper.updateAttributeFromDTO(existingAttribute, requestDTO);
            Attribute savedAttribute = attributeService.updateAttribute(updatedAttribute);

            AttributeResponseDTO responseDTO = DTOMapper.toResponseDTO(savedAttribute);
            return ResponseUtil.createResponse(HttpStatus.OK, responseDTO);

        } catch (JsonProcessingException e) {
            LoggingHelper.logJsonParsingError(logger, e.getMessage());
            return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Invalid JSON format");
        } catch (ValidationException e) {
            LoggingHelper.logValidationError(logger, e.getMessage());
            return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, e.toMap());
        } catch (AttributeNotFoundException e) {
            LoggingHelper.logEntityNotFound(logger, "Attribute", e.getMessage());
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

    private void logConnectionPoolStatusOnError() {
        try {
            ConnectionPoolManager poolManager = ConnectionPoolManager.getInstance();
            if (!poolManager.isHealthy()) {
                LoggingHelper.logConnectionPoolError(logger, 
                    poolManager.getPoolStats().toString(), false);
            }
        } catch (Exception e) {
            LoggingHelper.logConnectionPoolWarning(logger, 
                "Connection pool health check failed: " + e.getMessage());
        }
    }

}