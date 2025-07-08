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

import java.util.List;

public class GetCompaniesHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    
    private static final Logger logger = LoggerFactory.getLogger(GetCompaniesHandler.class);
    private static final CompanyService COMPANY_SERVICE = new CompanyService();
    
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String requestId = context.getAwsRequestId();
        MDC.put("requestId", requestId);
        
        try {
            logger.info("Starting companies retrieval process");
            
            List<Company> companies = COMPANY_SERVICE.getAllCompanies();
            logger.info("Retrieved {} companies successfully", companies.size());
            
            List<CompanyResponseDTO> responseDTOs = DTOMapper.toResponseDTOList(companies);
            
            return ResponseUtil.createResponse(200, responseDTOs);
            
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