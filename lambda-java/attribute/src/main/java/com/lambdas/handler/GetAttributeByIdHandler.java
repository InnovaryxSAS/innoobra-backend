package com.lambdas.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.lambdas.dto.response.AttributeResponseDTO;
import com.lambdas.exception.DatabaseException;
import com.lambdas.mapper.DTOMapper;
import com.lambdas.model.Attribute;
import com.lambdas.service.AttributeService;
import com.lambdas.service.impl.AttributeServiceImpl;
import com.lambdas.util.HttpStatus;
import com.lambdas.util.LoggingHelper;
import com.lambdas.util.ResponseUtil;
import org.slf4j.Logger;

import java.util.Optional;

public class GetAttributeByIdHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger logger = LoggingHelper.getLogger(GetAttributeByIdHandler.class);

    private final AttributeService attributeService;

    public GetAttributeByIdHandler() {
        this.attributeService = new AttributeServiceImpl();
    }

    // Constructor para inyección de dependencias (útil para testing)
    public GetAttributeByIdHandler(AttributeService attributeService) {
        this.attributeService = attributeService;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String requestId = context.getAwsRequestId();
        LoggingHelper.initializeRequestContext(requestId);

        try {
            if (input.getPathParameters() == null) {
                return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Attribute ID is required");
            }

            String attributeId = input.getPathParameters().get("id");
            if (attributeId == null || attributeId.trim().isEmpty()) {
                return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Attribute ID is required");
            }

            LoggingHelper.addAttributeId(attributeId);

            Optional<Attribute> attributeOpt = attributeService.getAttributeById(attributeId);

            if (attributeOpt.isPresent()) {
                AttributeResponseDTO responseDTO = DTOMapper.toResponseDTO(attributeOpt.get());
                return ResponseUtil.createResponse(HttpStatus.OK, responseDTO);
            } else {
                return ResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, "Attribute not found");
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