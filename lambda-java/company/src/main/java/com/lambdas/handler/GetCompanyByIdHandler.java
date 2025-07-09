package com.lambdas.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.lambdas.dto.response.CompanyResponseDTO;
import com.lambdas.exception.DatabaseException;
import com.lambdas.mapper.DTOMapper;
import com.lambdas.model.Company;
import com.lambdas.service.CompanyService;
import com.lambdas.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Optional;

public class GetCompanyByIdHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    
    private static final Logger logger = LoggerFactory.getLogger(GetCompanyByIdHandler.class);
    private static final CompanyService COMPANY_SERVICE = new CompanyService();
    
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String requestId = context.getAwsRequestId();
        MDC.put("requestId", requestId);
        
        try {
            logger.info("Starting company retrieval by ID process");
            
            String companyId = input.getPathParameters().get("id");
            if (companyId == null || companyId.trim().isEmpty()) {
                logger.warn("Company ID is missing or empty");
                return ResponseUtil.createErrorResponse(400, "Company ID is required");
            }
            
            MDC.put("companyId", companyId);
            
            Optional<Company> companyOpt = COMPANY_SERVICE.getCompanyById(companyId);
            
            if (companyOpt.isPresent()) {
                logger.info("Company retrieved successfully with ID: {}", companyId);
                
                CompanyResponseDTO responseDTO = DTOMapper.toResponseDTO(companyOpt.get());
                
                return ResponseUtil.createResponse(200, responseDTO);
            } else {
                logger.warn("Company not found with ID: {}", companyId);
                return ResponseUtil.createErrorResponse(404, "Company not found");
            }
            
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