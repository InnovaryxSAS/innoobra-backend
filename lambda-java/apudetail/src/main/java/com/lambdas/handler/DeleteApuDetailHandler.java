package com.lambdas.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.lambdas.dto.response.DeleteResponseDTO;
import com.lambdas.exception.ApuDetailNotFoundException;
import com.lambdas.exception.DatabaseException;
import com.lambdas.service.ApuDetailService;
import com.lambdas.service.impl.ApuDetailServiceImpl;
import com.lambdas.util.HttpStatus;
import com.lambdas.util.LoggingHelper;
import com.lambdas.util.ResponseUtil;
import org.slf4j.Logger;

public class DeleteApuDetailHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    
    private static final Logger logger = LoggingHelper.getLogger(DeleteApuDetailHandler.class);

    private final ApuDetailService apuDetailService;

    public DeleteApuDetailHandler() {
        this.apuDetailService = new ApuDetailServiceImpl();
    }

    // Constructor para inyección de dependencias (útil para testing)
    public DeleteApuDetailHandler(ApuDetailService apuDetailService) {
        this.apuDetailService = apuDetailService;
    }
    
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String requestId = context.getAwsRequestId();
        LoggingHelper.initializeRequestContext(requestId);
        
        try {
            String apuDetailId = input.getPathParameters().get("id");
            if (apuDetailId == null || apuDetailId.trim().isEmpty()) {
                return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "ApuDetail ID is required");
            }
            
            LoggingHelper.addApuDetailId(apuDetailId);
            
            boolean deleted = apuDetailService.deactivateApuDetail(apuDetailId);
            
            if (deleted) {
                DeleteResponseDTO responseDTO = new DeleteResponseDTO.Builder()
                        .message("ApuDetail successfully deactivated")
                        .apuDetailId(apuDetailId)
                        .success(true)
                        .build();
                
                return ResponseUtil.createResponse(HttpStatus.OK, responseDTO);
            } else {
                DeleteResponseDTO responseDTO = new DeleteResponseDTO.Builder()
                        .message("ApuDetail not found")
                        .apuDetailId(apuDetailId)
                        .success(false)
                        .build();
                
                return ResponseUtil.createResponse(HttpStatus.NOT_FOUND, responseDTO);
            }
            
        } catch (ApuDetailNotFoundException e) {
            LoggingHelper.logEntityNotFound(logger, "ApuDetail", e.getMessage());
            
            DeleteResponseDTO responseDTO = new DeleteResponseDTO.Builder()
                    .message(e.getMessage())
                    .apuDetailId(input.getPathParameters().get("id"))
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