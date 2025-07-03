package com.lambdas.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lambdas.dto.request.UpdateCompanyRequestDTO;
import com.lambdas.dto.response.CompanyResponseDTO;
import com.lambdas.exception.CompanyNotFoundException;
import com.lambdas.exception.DatabaseException;
import com.lambdas.exception.ValidationException;
import com.lambdas.mapper.DTOMapper;
import com.lambdas.model.Company;
import com.lambdas.repository.ConnectionPoolManager;
import com.lambdas.service.CompanyService;
import com.lambdas.util.ResponseUtil;
import com.lambdas.util.ValidationUtil;

import java.util.Optional;

public class UpdateCompanyHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    
    private static final CompanyService COMPANY_SERVICE = new CompanyService();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());
    
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
            
            if (input.getBody() == null || input.getBody().trim().isEmpty()) {
                return ResponseUtil.createErrorResponse(400, "Request body is required");
            }
            
            UpdateCompanyRequestDTO requestDTO = OBJECT_MAPPER.readValue(input.getBody(), UpdateCompanyRequestDTO.class);
            context.getLogger().log("Parsed update request DTO for company: " + companyId);
            
            // Fixed: Use the correct method name
            ValidationUtil.ValidationResult validationResult = ValidationUtil.validateUpdateRequest(requestDTO);
            if (!validationResult.isValid()) {
                return ResponseUtil.createErrorResponse(400, validationResult.getErrorsAsString());
            }
            
            Optional<Company> existingCompanyOpt = COMPANY_SERVICE.getCompanyById(companyId);
            if (!existingCompanyOpt.isPresent()) {
                return ResponseUtil.createErrorResponse(404, "Company not found");
            }
            
            Company existingCompany = existingCompanyOpt.get();
            
            Company updatedCompany = DTOMapper.updateCompanyFromDTO(existingCompany, requestDTO);
            
            Company savedCompany = COMPANY_SERVICE.updateCompany(updatedCompany);
            
            CompanyResponseDTO responseDTO = DTOMapper.toResponseDTO(savedCompany);
            
            context.getLogger().log("Company updated successfully: " + companyId);
            
            try {
                ConnectionPoolManager poolManager = ConnectionPoolManager.getInstance();
                context.getLogger().log("Final connection pool status: " + poolManager.getPoolStats());
            } catch (Exception e) {
                context.getLogger().log("Warning: Could not get final pool stats: " + e.getMessage());
            }
            
            return ResponseUtil.createResponse(200, responseDTO);
            
        } catch (JsonProcessingException e) {
            context.getLogger().log("JSON parsing error: " + e.getMessage());
            return ResponseUtil.createErrorResponse(400, "Invalid JSON format");
        } catch (ValidationException e) {
            context.getLogger().log("Validation error: " + e.getMessage());
            return ResponseUtil.createErrorResponse(400, e.getMessage());
        } catch (CompanyNotFoundException e) {
            context.getLogger().log("Company not found: " + e.getMessage());
            return ResponseUtil.createErrorResponse(404, e.getMessage());
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