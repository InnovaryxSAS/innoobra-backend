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

import java.util.List;

public class GetAttributeHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    
    private static final Logger logger = LoggingHelper.getLogger(GetAttributeHandler.class);

    private final AttributeService attributeService;

    public GetAttributeHandler() {
        this.attributeService = new AttributeServiceImpl();
    }

    // Constructor para inyección de dependencias (útil para testing)
    public GetAttributeHandler(AttributeService attributeService) {
        this.attributeService = attributeService;
    }
    
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String requestId = context.getAwsRequestId();
        LoggingHelper.initializeRequestContext(requestId);
        
        try {
            List<Attribute> attributes = attributeService.getAllAttributes();
            List<AttributeResponseDTO> responseDTOs = DTOMapper.toResponseDTOList(attributes);
            return ResponseUtil.createResponse(HttpStatus.OK, responseDTOs);
            
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