package com.lambdas.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.lambdas.dto.response.DeleteResponseDTO;
import com.lambdas.exception.CompanyNotFoundException;
import com.lambdas.exception.DatabaseException;
import com.lambdas.service.CompanyService;
import com.lambdas.service.impl.CompanyServiceImpl;
import com.lambdas.util.HttpStatus;
import com.lambdas.util.LoggingHelper;
import com.lambdas.util.ResponseUtil;
import org.slf4j.Logger;

public class DeleteCompanyHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    
    private static final Logger logger = LoggingHelper.getLogger(DeleteCompanyHandler.class);

    private final CompanyService companyService;

    public DeleteCompanyHandler() {
        this.companyService = new CompanyServiceImpl();
    }

    // Constructor para inyección de dependencias (útil para testing)
    public DeleteCompanyHandler(CompanyService companyService) {
        this.companyService = companyService;
    }
    
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String requestId = context.getAwsRequestId();
        LoggingHelper.initializeRequestContext(requestId);
        
        try {
            LoggingHelper.logProcessStart(logger, "company deletion");
            
            String companyId = input.getPathParameters().get("id");
            if (companyId == null || companyId.trim().isEmpty()) {
                LoggingHelper.logMissingParameter(logger, "Company ID");
                return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Company ID is required");
            }
            
            LoggingHelper.addCompanyId(companyId);
            
            boolean deleted = companyService.deleteCompany(companyId);
            
            if (deleted) {
                LoggingHelper.logSuccess(logger, "Company deletion", companyId);
                
                DeleteResponseDTO responseDTO = new DeleteResponseDTO.Builder()
                        .message("Company successfully deactivated")
                        .companyId(companyId)
                        .success(true)
                        .build();
                
                return ResponseUtil.createResponse(HttpStatus.OK, responseDTO);
            } else {
                LoggingHelper.logEntityNotFound(logger, "Company", companyId);
                
                DeleteResponseDTO responseDTO = new DeleteResponseDTO.Builder()
                        .message("Company not found")
                        .companyId(companyId)
                        .success(false)
                        .build();
                
                return ResponseUtil.createResponse(HttpStatus.NOT_FOUND, responseDTO);
            }
            
        } catch (CompanyNotFoundException e) {
            LoggingHelper.logEntityNotFound(logger, "Company", e.getMessage());
            
            DeleteResponseDTO responseDTO = new DeleteResponseDTO.Builder()
                    .message(e.getMessage())
                    .companyId(input.getPathParameters().get("id"))
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