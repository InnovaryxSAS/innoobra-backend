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
            String companyId = input.getPathParameters().get("id");
            if (companyId == null || companyId.trim().isEmpty()) {
                LoggingHelper.logMissingParameter(logger, "Company ID");
                return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Company ID is required");
            }

            LoggingHelper.addCompanyId(companyId);
            LoggingHelper.logProcessStart(logger, "company update");
            logConnectionPoolStatus();

            if (input.getBody() == null || input.getBody().trim().isEmpty()) {
                LoggingHelper.logEmptyRequestBody(logger);
                return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Request body is required");
            }

            UpdateCompanyRequestDTO requestDTO = OBJECT_MAPPER.readValue(input.getBody(),
                    UpdateCompanyRequestDTO.class);

            ValidationHelper.validateAndThrow(requestDTO, ValidationGroups.Update.class);

            Optional<Company> existingCompanyOpt = companyService.getCompanyById(companyId);
            if (!existingCompanyOpt.isPresent()) {
                LoggingHelper.logEntityNotFound(logger, "Company", companyId);
                return ResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, "Company not found");
            }

            Company existingCompany = existingCompanyOpt.get();

            Company updatedCompany = DTOMapper.updateCompanyFromDTO(existingCompany, requestDTO);
            Company savedCompany = companyService.updateCompany(updatedCompany);

            LoggingHelper.logSuccess(logger, "Company update", companyId);

            CompanyResponseDTO responseDTO = DTOMapper.toResponseDTO(savedCompany);

            logFinalConnectionPoolStatus();

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

    private void logConnectionPoolStatus() {
        try {
            ConnectionPoolManager poolManager = ConnectionPoolManager.getInstance();
            poolManager.getPoolStats();
            poolManager.isHealthy();
        } catch (Exception e) {
            LoggingHelper.logConnectionPoolWarning(logger,
                    "Could not retrieve connection pool status: " + e.getMessage());
        }
    }

    private void logFinalConnectionPoolStatus() {
        try {
            ConnectionPoolManager poolManager = ConnectionPoolManager.getInstance();
        } catch (Exception e) {
            LoggingHelper.logConnectionPoolWarning(logger,
                    "Could not retrieve final connection pool status: " + e.getMessage());
        }
    }

    private void logConnectionPoolStatusOnError() {
        try {
            ConnectionPoolManager poolManager = ConnectionPoolManager.getInstance();
            LoggingHelper.logConnectionPoolError(logger, poolManager.getPoolStats().toString(),
                    poolManager.isHealthy());
        } catch (Exception e) {
            LoggingHelper.logConnectionPoolWarning(logger,
                    "Could not retrieve connection pool status on error: " + e.getMessage());
        }
    }
}