package com.lambdas.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lambdas.dto.request.CreateCompanyRequestDTO;
import com.lambdas.dto.response.CompanyResponseDTO;
import com.lambdas.exception.CompanyAlreadyExistsException;
import com.lambdas.exception.DatabaseException;
import com.lambdas.exception.ValidationException;
import com.lambdas.mapper.DTOMapper;
import com.lambdas.model.Company;
import com.lambdas.service.CompanyService;
import com.lambdas.util.ResponseUtil;
import com.lambdas.util.ValidationHelper;
import com.lambdas.validation.groups.ValidationGroups;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class CreateCompanyHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger logger = LoggerFactory.getLogger(CreateCompanyHandler.class);
    private static final CompanyService COMPANY_SERVICE = new CompanyService();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String requestId = context.getAwsRequestId();
        MDC.put("requestId", requestId);
        
        try {
            logger.info("Starting company creation process");
            
            if (input.getBody() == null || input.getBody().trim().isEmpty()) {
                logger.warn("Request body is empty or null");
                return ResponseUtil.createErrorResponse(400, "Request body is required");
            }
            
            CreateCompanyRequestDTO requestDTO = OBJECT_MAPPER.readValue(input.getBody(), CreateCompanyRequestDTO.class);
            
            ValidationHelper.validateAndThrow(requestDTO, ValidationGroups.Create.class);
            
            Company company = DTOMapper.toCompany(requestDTO);
            
            Company createdCompany = COMPANY_SERVICE.createCompany(company);
            
            CompanyResponseDTO responseDTO = DTOMapper.toResponseDTO(createdCompany);
            
            return ResponseUtil.createResponse(201, responseDTO);
            
        } catch (JsonProcessingException e) {
            logger.error("JSON parsing error: {}", e.getMessage());
            return ResponseUtil.createErrorResponse(400, "Invalid JSON format");
        } catch (ValidationException e) {
            logger.warn("Validation error: {}", e.getMessage());
            return ResponseUtil.createErrorResponse(400, e.toMap());
        } catch (CompanyAlreadyExistsException e) {
            logger.warn("Company already exists: {}", e.getMessage());
            return ResponseUtil.createErrorResponse(409, e.getMessage());
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