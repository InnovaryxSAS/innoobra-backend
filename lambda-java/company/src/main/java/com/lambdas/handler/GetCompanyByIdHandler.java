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
import com.lambdas.service.impl.CompanyServiceImpl;
import com.lambdas.util.HttpStatus;
import com.lambdas.util.LoggingHelper;
import com.lambdas.util.ResponseUtil;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.UUID;

public class GetCompanyByIdHandler
        implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger logger = LoggingHelper.getLogger(GetCompanyByIdHandler.class);

    private final CompanyService companyService;

    public GetCompanyByIdHandler() {
        this.companyService = new CompanyServiceImpl();
    }

    // Constructor para inyección de dependencias (útil para testing)
    public GetCompanyByIdHandler(CompanyService companyService) {
        this.companyService = companyService;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String requestId = context.getAwsRequestId();
        LoggingHelper.initializeRequestContext(requestId);

        try {
            if (input.getPathParameters() == null) {
                return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Company ID is required");
            }

            String companyIdStr = input.getPathParameters().get("id");
            if (companyIdStr == null || companyIdStr.trim().isEmpty()) {
                return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Company ID is required");
            }

            LoggingHelper.addCompanyId(companyIdStr);

            UUID companyId;
            try {
                companyId = UUID.fromString(companyIdStr);
            } catch (IllegalArgumentException e) {
                return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Invalid company ID format");
            }

            Optional<Company> companyOpt = companyService.getCompanyById(companyId);

            if (companyOpt.isPresent()) {
                CompanyResponseDTO responseDTO = DTOMapper.toResponseDTO(companyOpt.get());
                return ResponseUtil.createResponse(HttpStatus.OK, responseDTO);
            } else {
                return ResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, "Company not found");
            }

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