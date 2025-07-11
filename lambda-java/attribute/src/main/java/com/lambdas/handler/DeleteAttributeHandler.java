package com.lambdas.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.lambdas.dto.response.DeleteResponseDTO;
import com.lambdas.exception.AttributeNotFoundException;
import com.lambdas.exception.DatabaseException;
import com.lambdas.service.AttributeService;
import com.lambdas.service.impl.AttributeServiceImpl;
import com.lambdas.util.HttpStatus;
import com.lambdas.util.LoggingHelper;
import com.lambdas.util.ResponseUtil;
import org.slf4j.Logger;

public class DeleteAttributeHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    
    private static final Logger logger = LoggingHelper.getLogger(DeleteAttributeHandler.class);

    private final AttributeService attributeService;

    public DeleteAttributeHandler() {
        this.attributeService = new AttributeServiceImpl();
    }

    // Constructor para inyección de dependencias (útil para testing)
    public DeleteAttributeHandler(AttributeService attributeService) {
        this.attributeService = attributeService;
    }
    
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String requestId = context.getAwsRequestId();
        LoggingHelper.initializeRequestContext(requestId);
        
        try {
            String attributeId = input.getPathParameters().get("id");
            if (attributeId == null || attributeId.trim().isEmpty()) {
                return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Attribute ID is required");
            }
            
            LoggingHelper.addAttributeId(attributeId);
            
            boolean deleted = attributeService.deactivateAttribute(attributeId);
            
            if (deleted) {
                DeleteResponseDTO responseDTO = new DeleteResponseDTO.Builder()
                        .message("Attribute successfully deactivated")
                        .attributeId(attributeId)
                        .success(true)
                        .build();
                
                return ResponseUtil.createResponse(HttpStatus.OK, responseDTO);
            } else {
                DeleteResponseDTO responseDTO = new DeleteResponseDTO.Builder()
                        .message("Attribute not found")
                        .attributeId(attributeId)
                        .success(false)
                        .build();
                
                return ResponseUtil.createResponse(HttpStatus.NOT_FOUND, responseDTO);
            }
            
        } catch (AttributeNotFoundException e) {
            LoggingHelper.logEntityNotFound(logger, "Attribute", e.getMessage());
            
            DeleteResponseDTO responseDTO = new DeleteResponseDTO.Builder()
                    .message(e.getMessage())
                    .attributeId(input.getPathParameters().get("id"))
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