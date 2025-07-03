package com.lambdas.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.lambdas.dto.response.DeleteResponseDTO;
import com.lambdas.exception.CompanyNotFoundException;
import com.lambdas.exception.DatabaseException;
import com.lambdas.repository.ConnectionPoolManager;
import com.lambdas.service.CompanyService;
import com.lambdas.util.ResponseUtil;

public class DeleteCompanyHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    
    private static final CompanyService COMPANY_SERVICE = new CompanyService();
    
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String requestId = context.getAwsRequestId();
        context.getLogger().log("Processing request: " + requestId);
        
        try {
            ConnectionPoolManager poolManager = ConnectionPoolManager.getInstance();
            context.getLogger().log("Connection pool status: " + poolManager.getPoolStats());
            context.getLogger().log("Connection pool healthy: " + poolManager.isHealthy());
        } catch (Exception e) {
            context.getLogger().log("Warning: Could not get pool stats: " + e.getMessage());
        }
        
        try {
            String companyId = input.getPathParameters().get("id");
            if (companyId == null || companyId.trim().isEmpty()) {
                return ResponseUtil.createErrorResponse(400, "Company ID is required");
            }
            
            context.getLogger().log("Attempting to delete company: " + companyId);
            
            boolean deleted = COMPANY_SERVICE.deleteCompany(companyId);
            
            if (deleted) {
                DeleteResponseDTO responseDTO = new DeleteResponseDTO.Builder()
                        .message("Company successfully deactivated")
                        .companyId(companyId)
                        .success(true)
                        .build();
                
                context.getLogger().log("Company deleted successfully: " + companyId);
                
                try {
                    ConnectionPoolManager poolManager = ConnectionPoolManager.getInstance();
                    context.getLogger().log("Final connection pool status: " + poolManager.getPoolStats());
                } catch (Exception e) {
                    context.getLogger().log("Warning: Could not get final pool stats: " + e.getMessage());
                }
                
                return ResponseUtil.createResponse(200, responseDTO);
            } else {
                DeleteResponseDTO responseDTO = new DeleteResponseDTO.Builder()
                        .message("Company not found")
                        .companyId(companyId)
                        .success(false)
                        .build();
                
                context.getLogger().log("Company not found for deletion: " + companyId);
                return ResponseUtil.createResponse(404, responseDTO);
            }
            
        } catch (CompanyNotFoundException e) {
            context.getLogger().log("Company not found: " + e.getMessage());
            
            DeleteResponseDTO responseDTO = new DeleteResponseDTO.Builder()
                    .message(e.getMessage())
                    .companyId(input.getPathParameters().get("id"))
                    .success(false)
                    .build();
                    
            return ResponseUtil.createResponse(404, responseDTO);
        } catch (DatabaseException e) {
            context.getLogger().log("Database error for request " + requestId + ": " + e.getMessage());
            try {
                ConnectionPoolManager poolManager = ConnectionPoolManager.getInstance();
                context.getLogger().log("Connection pool status on error: " + poolManager.getPoolStats());
                context.getLogger().log("Connection pool healthy on error: " + poolManager.isHealthy());
            } catch (Exception poolException) {
                context.getLogger().log("Could not get pool stats on error: " + poolException.getMessage());
            }
            return ResponseUtil.createErrorResponse(500, "Internal server error");
        } catch (Exception e) {
            context.getLogger().log("Unexpected error for request " + requestId + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseUtil.createErrorResponse(500, "Internal server error");
        }
    }
}