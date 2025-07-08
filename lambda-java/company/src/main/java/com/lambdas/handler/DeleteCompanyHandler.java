package com.lambdas.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.lambdas.dto.response.DeleteResponseDTO;
import com.lambdas.exception.CompanyNotFoundException;
import com.lambdas.exception.DatabaseException;
import com.lambdas.service.CompanyService;
import com.lambdas.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class DeleteCompanyHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    
    private static final Logger logger = LoggerFactory.getLogger(DeleteCompanyHandler.class);
    private static final CompanyService COMPANY_SERVICE = new CompanyService();
    
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String requestId = context.getAwsRequestId();
        MDC.put("requestId", requestId);
        
        try {
            logger.info("Starting company deletion process");
            
            String companyId = input.getPathParameters().get("id");
            if (companyId == null || companyId.trim().isEmpty()) {
                logger.warn("Company ID is missing or empty");
                return ResponseUtil.createErrorResponse(400, "Company ID is required");
            }
            
            MDC.put("companyId", companyId);
            
            boolean deleted = COMPANY_SERVICE.deleteCompany(companyId);
            
            if (deleted) {
                logger.info("Company deleted successfully with ID: {}", companyId);
                
                DeleteResponseDTO responseDTO = new DeleteResponseDTO.Builder()
                        .message("Company successfully deactivated")
                        .companyId(companyId)
                        .success(true)
                        .build();
                
                return ResponseUtil.createResponse(200, responseDTO);
            } else {
                logger.warn("Company not found for deletion with ID: {}", companyId);
                
                DeleteResponseDTO responseDTO = new DeleteResponseDTO.Builder()
                        .message("Company not found")
                        .companyId(companyId)
                        .success(false)
                        .build();
                
                return ResponseUtil.createResponse(404, responseDTO);
            }
            
        } catch (CompanyNotFoundException e) {
            logger.warn("Company not found: {}", e.getMessage());
            
            DeleteResponseDTO responseDTO = new DeleteResponseDTO.Builder()
                    .message(e.getMessage())
                    .companyId(input.getPathParameters().get("id"))
                    .success(false)
                    .build();
                    
            return ResponseUtil.createResponse(404, responseDTO);
        } catch (DatabaseException e) {
            logger.error("Database error occurred", e);
            return ResponseUtil.createErrorResponse(500, "Internal server error");
        } catch (Exception e) {
            logger.error("Unexpected error occurred", e);
            return ResponseUtil.createErrorResponse(500, "Internal server error");
        } finally {
            MDC.clear();
        }
    }
}