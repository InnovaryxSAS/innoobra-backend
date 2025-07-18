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
import com.lambdas.service.impl.CompanyServiceImpl;
import com.lambdas.util.HttpStatus;
import com.lambdas.util.LoggingHelper;
import com.lambdas.util.ResponseUtil;
import com.lambdas.util.ValidationHelper;
import com.lambdas.validation.groups.ValidationGroups;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.UUID;

public class UpdateCompanyHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger logger = LoggingHelper.getLogger(UpdateCompanyHandler.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private final CompanyService companyService;

    public UpdateCompanyHandler() {
        this.companyService = new CompanyServiceImpl();
    }

    // Constructor para inyección de dependencias (útil para testing)
    public UpdateCompanyHandler(CompanyService companyService) {
        this.companyService = companyService;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String requestId = context.getAwsRequestId();
        LoggingHelper.initializeRequestContext(requestId);

        try {
            String companyIdStr = null;
            if (input.getPathParameters() != null) {
                companyIdStr = input.getPathParameters().get("id");
            }
            
            if (companyIdStr == null || companyIdStr.trim().isEmpty()) {
                return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Company ID is required");
            }

            LoggingHelper.addCompanyId(companyIdStr);

            UUID companyId;
            try {
                companyId = UUID.fromString(companyIdStr);
            } catch (IllegalArgumentException e) {
                LoggingHelper.logValidationError(logger, "Invalid UUID format: " + companyIdStr);
                return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Invalid company ID format");
            }

            if (input.getBody() == null || input.getBody().trim().isEmpty()) {
                return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Request body is required");
            }

            UpdateCompanyRequestDTO requestDTO = OBJECT_MAPPER.readValue(input.getBody(),
                    UpdateCompanyRequestDTO.class);

            ValidationHelper.validateAndThrow(requestDTO, ValidationGroups.Update.class);

            Optional<Company> existingCompanyOpt = companyService.getCompanyById(companyId);
            if (!existingCompanyOpt.isPresent()) {
                LoggingHelper.logEntityNotFound(logger, "Company", companyIdStr);
                return ResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, "Company not found");
            }

            Company existingCompany = existingCompanyOpt.get();
            Company updatedCompany = DTOMapper.updateCompanyFromDTO(existingCompany, requestDTO);
            Company savedCompany = companyService.updateCompany(updatedCompany);

            CompanyResponseDTO responseDTO = DTOMapper.toResponseDTO(savedCompany);
            return ResponseUtil.createResponse(HttpStatus.OK, responseDTO);

        } catch (JsonProcessingException e) {
            LoggingHelper.logJsonParsingError(logger, e.getMessage());
            return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Invalid JSON format");
        } catch (ValidationException e) {
            LoggingHelper.logValidationError(logger, e.getMessage());
            return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, e.toMap());
        } catch (CompanyNotFoundException e) {
            LoggingHelper.logEntityNotFound(logger, "Company", e.getMessage());
            return ResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (DatabaseException e) {
            LoggingHelper.logDatabaseError(logger, e.getMessage(), e);
            logConnectionPoolStatusOnError();
            return ResponseUtil.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        } catch (Exception e) {
            LoggingHelper.logUnexpectedError(logger, e.getMessage(), e);
            return ResponseUtil.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        } finally {
            LoggingHelper.clearContext();
        }
    }

    private void logConnectionPoolStatusOnError() {
        try {
            ConnectionPoolManager poolManager = ConnectionPoolManager.getInstance();
            if (!poolManager.isHealthy()) {
                LoggingHelper.logConnectionPoolError(logger, 
                    poolManager.getPoolStats().toString(), false);
            }
        } catch (Exception e) {
            LoggingHelper.logConnectionPoolWarning(logger, 
                "Connection pool health check failed: " + e.getMessage());
        }
    }
}