package com.lambdas.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lambdas.dto.request.UpdateApuDetailRequestDTO;
import com.lambdas.dto.response.ApuDetailResponseDTO;
import com.lambdas.exception.ApuDetailNotFoundException;
import com.lambdas.exception.DatabaseException;
import com.lambdas.exception.ValidationException;
import com.lambdas.mapper.DTOMapper;
import com.lambdas.model.ApuDetail;
import com.lambdas.repository.ConnectionPoolManager;
import com.lambdas.service.ApuDetailService;
import com.lambdas.service.impl.ApuDetailServiceImpl;
import com.lambdas.util.HttpStatus;
import com.lambdas.util.LoggingHelper;
import com.lambdas.util.ResponseUtil;
import com.lambdas.util.ValidationHelper;
import com.lambdas.validation.groups.ValidationGroups;
import org.slf4j.Logger;

import java.util.Optional;

public class UpdateApuDetailHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger logger = LoggingHelper.getLogger(UpdateApuDetailHandler.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private final ApuDetailService apuDetailService;

    public UpdateApuDetailHandler() {
        this.apuDetailService = new ApuDetailServiceImpl();
    }

    // Constructor para inyección de dependencias (útil para testing)
    public UpdateApuDetailHandler(ApuDetailService apuDetailService) {
        this.apuDetailService = apuDetailService;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String requestId = context.getAwsRequestId();
        LoggingHelper.initializeRequestContext(requestId);

        try {
            String apuDetailId = null;
            if (input.getPathParameters() != null) {
                apuDetailId = input.getPathParameters().get("id");
            }
            
            if (apuDetailId == null || apuDetailId.trim().isEmpty()) {
                return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "ApuDetail ID is required");
            }

            LoggingHelper.addApuDetailId(apuDetailId);

            if (input.getBody() == null || input.getBody().trim().isEmpty()) {
                return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Request body is required");
            }

            UpdateApuDetailRequestDTO requestDTO = OBJECT_MAPPER.readValue(input.getBody(), UpdateApuDetailRequestDTO.class);

            ValidationHelper.validateAndThrow(requestDTO, ValidationGroups.Update.class);

            Optional<ApuDetail> existingApuDetailOpt = apuDetailService.getApuDetailById(apuDetailId);
            if (!existingApuDetailOpt.isPresent()) {
                LoggingHelper.logEntityNotFound(logger, "ApuDetail", apuDetailId);
                return ResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, "ApuDetail not found");
            }

            ApuDetail existingApuDetail = existingApuDetailOpt.get();
            ApuDetail updatedApuDetail = DTOMapper.updateApuDetailFromDTO(existingApuDetail, requestDTO);
            ApuDetail savedApuDetail = apuDetailService.updateApuDetail(updatedApuDetail);

            ApuDetailResponseDTO responseDTO = DTOMapper.toResponseDTO(savedApuDetail);
            return ResponseUtil.createResponse(HttpStatus.OK, responseDTO);

        } catch (JsonProcessingException e) {
            LoggingHelper.logJsonParsingError(logger, e.getMessage());
            return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Invalid JSON format");
        } catch (ValidationException e) {
            LoggingHelper.logValidationError(logger, e.getMessage());
            return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, e.toMap());
        } catch (ApuDetailNotFoundException e) {
            LoggingHelper.logEntityNotFound(logger, "ApuDetail", e.getMessage());
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